package com.example.application.views.añadirmedicina;

import com.example.application.data.entity.Medicina;
import com.example.application.data.service.MedicinaService;
import com.example.application.views.MainLayout;
import com.example.application.views.medicinas.Medicamento;
import com.example.application.views.medicinas.MedicinasAPI;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.PermitAll;

@PageTitle("Añadir Medicina")
@Route(value = "addMedi", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class AñadirMedicinaView extends Div {


    private TextField nombreMed = new TextField("Nombre");
    private TextField descripcionMed = new TextField("Descripción");
    private IntegerField cantidadToma= new IntegerField("Cantidad por toma");
    private IntegerField cantidadCaja= new IntegerField("Cantidad restante");
    Checkbox l = new Checkbox();
    Checkbox m = new Checkbox();
    Checkbox x = new Checkbox();
    Checkbox j = new Checkbox();
    Checkbox v = new Checkbox();
    Checkbox s = new Checkbox();
    Checkbox d = new Checkbox();

    private IntegerField horas = new IntegerField("Intervalo entre tomas");


    private Button cancel = new Button("Cancelar");
    private Button save = new Button("Guardar");

    private Binder<Medicina> binder = new Binder<>(Medicina.class);

    MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
    Upload upload = new Upload(buffer);

    public AñadirMedicinaView(MedicinaService personService) {
        addClassName("añadir-medicina-view");

        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());

        binder.bindInstanceFields(this);
        clearForm();

        cantidadCaja.setHasControls(true);
        cantidadToma.setHasControls(true);
        horas.setHasControls(true);
        horas.setMin(0);
        horas.setMax(24);
        cantidadToma.setMin(0);
        cantidadCaja.setMin(0);
        l.setLabel("Lunes");
        m.setLabel("Martes");
        x.setLabel("Miércoles");
        j.setLabel("Jueves");
        v.setLabel("Viernes");
        s.setLabel("Sábado");
        d.setLabel("Domingo");

        upload.setAcceptedFileTypes("application/jpg", ".jpg", ".png", "jpeg");
        upload.addFileRejectedListener(event -> {
            String errorMessage = event.getErrorMessage();

            Notification notification = Notification.show(
                    errorMessage,
                    3000,
                    Notification.Position.MIDDLE
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });

        cancel.addClickListener(e -> clearForm());
        save.addClickListener(e -> {
            personService.update(binder.getBean());


//#####################################################################################################################

            String temp = "";
            String diasdetoma = "";
            if (l.getValue() == true){
                temp = "L";
                diasdetoma = diasdetoma + temp;
            }
            if (m.getValue() == true){
                temp = "M";
                diasdetoma = diasdetoma + temp;
            }
            if (x.getValue() == true){
                temp = "X";
                diasdetoma = diasdetoma + temp;
            }
            if (j.getValue() == true){
                temp = "J";
                diasdetoma = diasdetoma + temp;
            }
            if (v.getValue() == true){
                temp = "V";
                diasdetoma = diasdetoma + temp;
            }
            if (s.getValue() == true){
                temp = "S";
                diasdetoma = diasdetoma + temp;
            }
            if (d.getValue() == true){
                temp = "D";
                diasdetoma = diasdetoma + temp;
            }


//#####################################################################################################################

            if (nombreMed.getValue() == "" || descripcionMed.getValue() == "" ||
                    diasdetoma == "" || horas.getValue() == 0 || cantidadToma.getValue() == 0 || cantidadCaja
                    .getValue() == 0){
                Notification notification = Notification.show("Rellene todos los campos");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
            else {
                Medicamento medicina = new Medicamento(1, nombreMed.getValue(), descripcionMed.getValue(),
                        diasdetoma, horas.getValue(), cantidadToma.getValue(), cantidadCaja
                        .getValue());

                MedicinasAPI apimedicina = new MedicinasAPI();

                apimedicina.aniadirMedicina(medicina);

                Notification notification = Notification.show("Medicamento añadido");
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);


                l.clear();
                m.clear();
                x.clear();
                j.clear();
                v.clear();
                s.clear();
                d.clear();
                clearForm();
            }
        });
    }

    private void clearForm() {
        binder.setBean(new Medicina());
    }

    private Component createTitle() {
        return new H3("Introduce los datos de la medicina");
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.add(nombreMed, descripcionMed, cantidadToma, cantidadCaja, horas, l, m, x, j, v, s, d, upload);
        formLayout.setColspan(l, 2);
        formLayout.setColspan(m, 2);
        formLayout.setColspan(x, 2);
        formLayout.setColspan(j, 2);
        formLayout.setColspan(v, 2);
        formLayout.setColspan(s, 2);
        formLayout.setColspan(d, 2);
        return formLayout;
    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save);
        buttonLayout.add(cancel);
        return buttonLayout;
    }

}
