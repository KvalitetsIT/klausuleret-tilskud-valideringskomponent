package dk.kvalitetsit.itukt.common;

import dk.kvalitetsit.itukt.common.configuration.CommonConfiguration;
import dk.kvalitetsit.itukt.common.configuration.ConnectionConfiguration;
import dk.kvalitetsit.itukt.common.configuration.DatasourceConfiguration;

import java.util.List;

public class Mock {

    public static final CommonConfiguration CONFIG = new CommonConfiguration(
            List.of(""),
            new DatasourceConfiguration("", "", "",
                    new ConnectionConfiguration("", null, null)
            )
    );
}
