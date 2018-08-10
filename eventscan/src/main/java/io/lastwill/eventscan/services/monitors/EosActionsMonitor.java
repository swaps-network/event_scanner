package io.lastwill.eventscan.services.monitors;

import io.lastwill.eventscan.events.model.CreateAccountEvent;
import io.lastwill.eventscan.events.model.CreateTokenEvent;
import io.lastwill.eventscan.messages.AccountCreatedNotify;
import io.lastwill.eventscan.messages.PaymentStatus;
import io.lastwill.eventscan.messages.TokenCreatedNotify;
import io.lastwill.eventscan.model.NetworkType;
import io.lastwill.eventscan.repositories.ContractRepository;
import io.lastwill.eventscan.services.ExternalNotifier;
import io.lastwill.eventscan.services.TransactionProvider;
import io.mywish.scanner.model.NewBlockEvent;
import io.mywish.wrapper.WrapperTransaction;
import io.mywish.wrapper.WrapperTransactionReceipt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EosActionsMonitor {
    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ExternalNotifier externalNotifier;

    @Autowired
    private TransactionProvider transactionProvider;

    @EventListener
    private void onNewBlockEvent(final NewBlockEvent event) {
        if (event.getNetworkType() != NetworkType.EOS_MAINNET && event.getNetworkType() != NetworkType.EOS_TESTNET) {
            return;
        }

        Map<String, WrapperTransaction> txByHash = event.getBlock()
                .getTransactions()
                .stream()
                .collect(Collectors.toMap(WrapperTransaction::getHash, Function.identity()));

        if (txByHash.isEmpty()) {
            return;
        }

        contractRepository.findByTxHashes(txByHash.keySet(), event.getNetworkType())
                .forEach(contract -> {
                    WrapperTransaction wrapperTransaction = txByHash.get(contract.getTxHash());
                    if (wrapperTransaction == null) {
                        log.error("Contract {} was selected from DB by tx hash, but there is no corresponds tx.", contract.getId());
                        return;
                    }

                    WrapperTransactionReceipt receipt;
                    try {
                        receipt = transactionProvider.getTransactionReceipt(event.getNetworkType(), wrapperTransaction);
                    }
                    catch (Exception e) {
                        log.error("Error on getting transaction receipt for tx {}.", wrapperTransaction.getHash(), e);
                        return;
                    }

                    receipt.getLogs()
                            .forEach(contractEvent -> {
                                if (contractEvent instanceof CreateAccountEvent) {
                                    CreateAccountEvent createAccountEvent = (CreateAccountEvent) contractEvent;
                                    externalNotifier.send(event.getNetworkType(),
                                            new AccountCreatedNotify(
                                                    contract.getId(),
                                                    receipt.isSuccess() ? PaymentStatus.COMMITTED : PaymentStatus.REJECTED,
                                                    wrapperTransaction.getHash(),
                                                    createAccountEvent.getName()
                                            ));

                                }
                                else if (contractEvent instanceof CreateTokenEvent) {
                                    CreateTokenEvent createTokenEvent = (CreateTokenEvent) contractEvent;
                                    externalNotifier.send(event.getNetworkType(),
                                            new TokenCreatedNotify(
                                                    contract.getId(),
                                                    receipt.isSuccess() ? PaymentStatus.COMMITTED : PaymentStatus.REJECTED,
                                                    wrapperTransaction.getHash(),
                                                    createTokenEvent.getAddress()
                                            ));
                                }
                            });
                });

    }
}
