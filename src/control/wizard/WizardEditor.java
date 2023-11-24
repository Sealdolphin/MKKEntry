package control.wizard;

import data.wizard.WizardType;

import javax.swing.*;
import java.util.List;
import java.util.function.Consumer;

public abstract class WizardEditor<T extends WizardType> {

    protected T data;
    private final T initialData;

    private List<Consumer<?>> setters;

    public WizardEditor(T data) {
        this.data = data;
        this.initialData = data;
    }

    public abstract JPanel createView();

    public abstract T createNew();

    public void addConsumer(Consumer<?> consumer) {
        setters.add(consumer);
    }

    public T getInitialModel() {
        return initialData;
    }



}
