package org.ballerinax.asb.manager;

import com.microsoft.azure.servicebus.management.ManagementClient;
import com.microsoft.azure.servicebus.management.MessageCountDetails;
import com.microsoft.azure.servicebus.management.NamespaceInfo;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import com.microsoft.azure.servicebus.primitives.ServiceBusException;
import io.ballerina.runtime.api.creators.ValueCreator;
import io.ballerina.runtime.api.utils.StringUtils;
import io.ballerina.runtime.api.values.BMap;
import io.ballerina.runtime.api.values.BString;
import org.ballerinax.asb.util.ASBConstants;
import org.ballerinax.asb.util.ASBUtils;
import org.ballerinax.asb.util.ModuleUtils;

public class ASBManager {
    ManagementClient managementClient;

    public ASBManager(String connectionString) {
        this.managementClient = new ManagementClient(new ConnectionStringBuilder(connectionString));
    }

    public Object getNamespaceInfo() {
        try {
            NamespaceInfo namespaceInfo = managementClient.getNamespaceInfo();
            Object[] values = new Object[1];
            values[0] = StringUtils.fromString(namespaceInfo.getName());
            BMap<BString, Object> namespaceRecord =
                    ValueCreator.createRecordValue(ModuleUtils.getModule(), ASBConstants.NAMESPACE_RECORD);
            return ValueCreator.createRecordValue(namespaceRecord, values);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (ServiceBusException e) {
            return ASBUtils.returnErrorValue("Exception while receiving namespace information" + e.getMessage());
        }
    }

    public Object getMessageCountDetailsOfQueue(String entityPath) {
        try {
            MessageCountDetails messageCountDetails = managementClient.getQueueRuntimeInfo(entityPath).getMessageCountDetails();
            Object[] values = new Object[5];
            values[0] = messageCountDetails.getActiveMessageCount();
            values[1] = messageCountDetails.getDeadLetterMessageCount();
            values[2] = messageCountDetails.getScheduledMessageCount();
            values[3] = messageCountDetails.getTransferMessageCount();
            values[4] = messageCountDetails.getTransferDeadLetterMessageCount();
            BMap<BString, Object> messageCountDetailsRecord =
                    ValueCreator.createRecordValue(ModuleUtils.getModule(), ASBConstants.MESSAGE_COUNT_DETAILS_RECORD);
            return ValueCreator.createRecordValue(messageCountDetailsRecord, values);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (ServiceBusException e) {
            return ASBUtils.returnErrorValue("Exception while receiving namespace information" + e.getMessage());
        }
    }
}
