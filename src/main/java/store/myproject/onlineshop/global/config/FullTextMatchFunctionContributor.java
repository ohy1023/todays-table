package store.myproject.onlineshop.global.config;


import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;

import static org.hibernate.type.StandardBasicTypes.BOOLEAN;


public class FullTextMatchFunctionContributor implements FunctionContributor {

    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        functionContributions
                .getFunctionRegistry()
                .registerPattern("fulltext_match", "MATCH(?1) AGAINST(?2)",
                        functionContributions.getTypeConfiguration().getBasicTypeRegistry().resolve(BOOLEAN));
    }

    @Override
    public int ordinal() {
        return FunctionContributor.super.ordinal();
    }
}
