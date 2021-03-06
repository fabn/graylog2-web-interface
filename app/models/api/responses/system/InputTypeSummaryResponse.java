/**
 * Copyright 2013 Lennart Koopmann <lennart@torch.sh>
 *
 * This file is part of Graylog2.
 *
 * Graylog2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog2.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package models.api.responses.system;

import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import lib.plugin.configuration.BooleanField;
import lib.plugin.configuration.NumberField;
import lib.plugin.configuration.RequestedConfigurationField;
import lib.plugin.configuration.TextField;
import play.Logger;

import java.util.List;
import java.util.Map;

/**
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class InputTypeSummaryResponse {

    public String name;
    public String type;

    @SerializedName("human_name")
    public String humanName;

    @SerializedName("is_exclusive")
    public boolean isExclusive;

    @SerializedName("requested_configuration")
    public Map<String, Map<String, Object>> requestedConfiguration;

    public List<RequestedConfigurationField> getRequestedConfiguration() {
        List<RequestedConfigurationField> fields = Lists.newArrayList();
        List<RequestedConfigurationField> tmpBools = Lists.newArrayList();

        for (Map.Entry<String, Map<String, Object>> c : requestedConfiguration.entrySet()) {
            try {
                String fieldType = (String) c.getValue().get("type");
                switch(fieldType) {
                    case "text":
                        fields.add(new TextField(c));
                        continue;
                    case "number":
                        fields.add(new NumberField(c));
                        continue;
                    case "boolean":
                        tmpBools.add(new BooleanField(c));
                        continue;
                    default:
                        Logger.info("Unknown field type [" + fieldType + "].");
                }
            } catch (Exception e) {
                Logger.error("Skipping invalid configuration field [" + c.getKey() + "]", e);
                continue;
            }
        }

        // We want the boolean fields at the end for display/layout reasons.
        fields.addAll(tmpBools);

        return fields;
    }

}
