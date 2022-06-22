package com.example.application.views.medicinas;

import com.example.application.data.entity.Medicina;
import com.example.application.views.MainLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.annotation.security.PermitAll;

@PageTitle("Medicinas")
@Route(value = "medic", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class MedicinasView extends Div {

    private GridPro<Medicamento> grid;
    private GridListDataView<Medicamento> gridListDataView;

    private Grid.Column<Medicamento> nombreMedColumn;
    private Grid.Column<Medicamento> descripcionMedColumn;
    private Grid.Column<Medicamento> daysColumn;
    private Grid.Column<Medicamento> horasColumn;
    private Grid.Column<Medicamento> cantidaTomaColumn;
    private Grid.Column<Medicamento> cantidadCajaColumn;

    private Button eliminar = new Button("Eliminar seleecionado");
    private Button correo = new Button("Enviar correo");


    public MedicinasView() {
        eliminar.setEnabled(false);
        eliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        addClassName("medicinas-view");
        setSizeFull();
        createGrid();
        add(grid, eliminar, correo);
        grid.addSelectionListener(selection -> {
            int size = selection.getAllSelectedItems().size();
            boolean isSingleSelection = size == 1;
            eliminar.setEnabled(size != 0);
            eliminar.addClickListener(e->{
                //insertar codigo que llama a la api y elimina lo que haya
                Set<Medicamento> medicinaseliminar = selection.getAllSelectedItems();
                MedicinasAPI apiMedicina = new MedicinasAPI();

                for(Medicamento i : medicinaseliminar){
                    apiMedicina.eliminarMedicamento(i.getId_medicamento());
                }

                UI.getCurrent().getPage().reload();
            });
        });
        correo.addClickListener(e->{
            sendEmail();
            UI.getCurrent().getPage().reload();
        });
    }

    private void createGrid() {
        createGridComponent();
        addColumnsToGrid();
        //addFiltersToGrid();
    }

    private void createGridComponent() {
        grid = new GridPro<>();
        grid.setSelectionMode(SelectionMode.MULTI);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COLUMN_BORDERS);
        grid.setHeight("95%");

        List<Medicamento> meds = getClients();
        gridListDataView = grid.setItems(meds);
    }

    private void addColumnsToGrid() {
        createnombreMedColumn();
        createdecripcionMedColumn();
        createdaysColumn();
        createHorasColumn();
        createcantidadTomaColumn();
        createcantidadCajaColumn();
    }

    private void createnombreMedColumn() {
        nombreMedColumn = grid.addColumn(new ComponentRenderer<>(medicamento -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(Alignment.CENTER);
            //Image img = new Image(client.getImg(), "");
            Span span = new Span();
            span.setClassName("name");
            span.setText(medicamento.getNombre());
            hl.add(span);
            return hl;
        })).setComparator(medicamento -> medicamento.getNombre()).setHeader("Nombre");
    }

    private void createdecripcionMedColumn() {
        descripcionMedColumn = grid.addColumn(new ComponentRenderer<>(medicamento -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(Alignment.CENTER);
            Span span = new Span();
            span.setClassName("name");
            span.setText(medicamento.getDescripcion());
            hl.add(span);
            return hl;
        })).setComparator(medicamento -> medicamento.getDescripcion()).setHeader("Descripción");
    }

    private void createdaysColumn() {
        daysColumn = grid.addColumn(new ComponentRenderer<>(medicamento -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(Alignment.CENTER);
            //Image img = new Image(client.getImg(), "");
            Span span = new Span();
            span.setClassName("name");
            span.setText(medicamento.getDias_semana());
            hl.add(span);
            return hl;
        })).setComparator(medicamento -> medicamento.getDias_semana()).setHeader("Dias");
    }

    private void createHorasColumn() {
        horasColumn = cantidaTomaColumn = grid.addColumn(Medicamento::getHoras_entre_toma).setHeader("Intervalo de horas");
    }

    private void createcantidadTomaColumn() {
        cantidaTomaColumn = grid.addColumn(Medicamento::getPastillas_toma).setHeader("Cantidad por toma");
    }

    private void createcantidadCajaColumn() {
        cantidadCajaColumn = grid.addColumn(Medicamento::getPastillas_caja).setHeader("Cantidad por caja");
    }



    private List<Medicamento> getClients() {


        /*Pruebas de otras api */

        MedicinasAPI apiMedicina = new MedicinasAPI();
        List<Medicamento> medicinas_usuario = apiMedicina.recibirMedicinasUsuario(1);

        return medicinas_usuario;

    }

    @Autowired
    private EmailSenderService senderService;

    public String infomail() {

        String nombre;
        int horas_entre_toma;
        int pastillas_toma;
        String descripcion;
        String dias_semana;
        int pastillas_caja;

        List<Medicamento> Listamed = getClients();
        System.out.println("klk:" + Listamed);

        String Mensaje = "";


        Date fechaActual = new Date();
        String fecha = fechaActual.toString();
        String[] res = fecha.split("[ ]", 0);
        String diaactual = res[0];
        char diahoy = ' ';
        if (diaactual.equals("Mon")) {
            diahoy = 'L';
        } else if (diaactual.equals("Tue")) {
            diahoy = 'M';
        } else if (diaactual.equals("Wed")) {
            diahoy = 'X';
        } else if (diaactual.equals("Thu")) {
            diahoy = 'J';
        } else if (diaactual.equals("Fri")) {
            diahoy = 'V';
        } else if (diaactual.equals("Sat")) {
            diahoy = 'S';
        } else if (diaactual.equals("Sun")) {
            diahoy = 'D';
        }

        for (Medicamento medicamento : Listamed) {
            dias_semana = medicamento.getDias_semana();
            for (int i = 0; i < dias_semana.length(); i++) {
                char dia = dias_semana.charAt(i);
                if (dia == diahoy) {
                    String info = "\n\n----------------------------------------------------------\n" +
                            "La información del medicamento a tomar es la siguiente:\n";
                    nombre = medicamento.getNombre();
                    horas_entre_toma = medicamento.getHoras_entre_toma();
                    pastillas_toma = medicamento.getPastillas_toma();
                    descripcion = medicamento.getDescripcion();
                    pastillas_caja = medicamento.getPastillas_caja();

                    info = info + nombre;
                    info = info + "\n" + descripcion + "\nDeben pasar " + horas_entre_toma + "h entre tomas\nDebe tomar " + pastillas_toma
                            + " pastillas\nDebe tomarlo el día: " + dias_semana + "\nLe quedan " + pastillas_caja + " pastillas en la caja";

                    Mensaje = Mensaje + info;

                }
            }
        }
        System.out.println(Mensaje);
        return Mensaje;
    }

    //@EventListener(ApplicationReadyEvent.class)
    public void sendEmail() {

        senderService.sendEmail("jgarciaff@gmail.com",//pastillerodigital2022@gmail.com
                "Notificación pastillero",
                infomail());
    }


};
