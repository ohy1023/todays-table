package store.myproject.onlineshop.global.config;


import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.type.StandardBasicTypes;


public class NgramFullTextMatchFunctionContributor implements FunctionContributor {

    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        functionContributions.getFunctionRegistry().registerPattern(
                "ngram_match",
                "MATCH(?1) AGAINST(?2 IN NATURAL LANGUAGE MODE)",
                functionContributions.getTypeConfiguration()
                        .getBasicTypeRegistry()
                        .resolve(StandardBasicTypes.DOUBLE)
        );
    }

    @Override
    public int ordinal() {
        return FunctionContributor.super.ordinal();
    }
}
