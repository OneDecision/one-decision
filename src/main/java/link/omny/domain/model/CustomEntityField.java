package link.omny.domain.model;

public class CustomEntityField extends EntityField {

    public CustomEntityField(String name, String label, String hint,
            boolean required, String type) {
        super(name, label, hint, required, type);
        setBuiltIn(false);
    }

    public CustomEntityField(String name, String label, String hint,
            boolean required, String type, String validation) {
        super(name, label, hint, required, type, validation);
        setBuiltIn(false);
    }
}
