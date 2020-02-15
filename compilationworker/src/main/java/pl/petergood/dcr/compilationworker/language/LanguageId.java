package pl.petergood.dcr.compilationworker.language;

public enum LanguageId {
    CPP("CPP");

    private String id;

    LanguageId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return getId();
    }
}
