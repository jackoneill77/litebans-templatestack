package xyz.jackoneill.litebans.templatestack.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Template {
    private int index;
    private String template;
    private int punishments;
    private TemplateType type;

    public Template(String template, int punishments, TemplateType type) {
        this.index = -1;
        this.template = template;
        this.punishments = punishments;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Template(" + this.template
                + "): type=" + this.type
                + " punishments=" + this.punishments
                + " idx=" + this.index;
    }
}
