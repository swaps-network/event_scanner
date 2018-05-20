package io.lastwill.eventscan.services.builders;

import io.mywish.wrapper.ContractEventBuilder;
import io.mywish.wrapper.WrapperTransactionReceipt;
import io.mywish.wrapper.WrapperType;
import io.mywish.wrapper.ContractEventDefinition;
import io.lastwill.eventscan.events.contract.KilledEvent;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.web3j.abi.datatypes.Bool;
import java.util.Arrays;
import java.util.List;

@Getter
@Component
public class KilledEventBuilder extends ContractEventBuilder<KilledEvent> {
    private final ContractEventDefinition definition = new ContractEventDefinition(
            "Killed",
            Arrays.asList(WrapperType.create(Bool.class, false))
    );

    @Override
    public KilledEvent build(WrapperTransactionReceipt transactionReceipt, String address, List<Object> values) {
        return new KilledEvent(definition, transactionReceipt, (Boolean) values.get(0), address);
    }
}
