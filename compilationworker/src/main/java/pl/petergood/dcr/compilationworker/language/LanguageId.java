package pl.petergood.dcr.compilationworker.language;

public enum LanguageId {
    CPP("CPP", "cpp");

    private String id;
    private String extension;

    LanguageId(String id, String extension) {
        this.id = id;
        this.extension = extension;
    }

    public String getId() {
        return this.id;
    }

    public String getExtension() {
        return extension;
    }

    @Override
    public String toString() {
        return getId();
    }

    public static LanguageId fromId(String id) {
        for (LanguageId languageId : LanguageId.values()) {
            if (languageId.getId().equals(id)) {
                return languageId;
            }
        }

        throw new IllegalArgumentException("Language with given id does not exist.");
    }
}
