import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Ventana extends JFrame {
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField txtIdentificacion;
    private JTextField txtNombre;
    private JTextField txtCorreo;
    private JButton btnGuardarPlano, btnLeerPlano, btnGuardarXML, btnLeerXML, btnGuardarJSON, btnLeerJSON, btnAgregar;

    public Ventana() {
        setTitle("Gestión de Personas");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Tabla para visualizar datos
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Correo"}, 0);
        tabla = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tabla);
        add(scrollPane, BorderLayout.CENTER);

        // Panel del formulario reorganizado
JPanel panelFormulario = new JPanel();
GroupLayout layout = new GroupLayout(panelFormulario);
panelFormulario.setLayout(layout);

// Configuración de auto-creación de gaps
layout.setAutoCreateGaps(true);
layout.setAutoCreateContainerGaps(true);

// Creación de los componentes
JLabel lblIdentificacion = new JLabel("Identificación:");
txtIdentificacion = new JTextField();

JLabel lblNombre = new JLabel("Nombre:");
txtNombre = new JTextField();

JLabel lblCorreo = new JLabel("Correo:");
txtCorreo = new JTextField();

btnAgregar = new JButton("Agregar a la Tabla");

// Posicionamiento de los componentes en el GroupLayout
layout.setHorizontalGroup(
    layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(lblIdentificacion)
            .addComponent(lblNombre)
            .addComponent(lblCorreo))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(txtIdentificacion)
            .addComponent(txtNombre)
            .addComponent(txtCorreo)
            .addComponent(btnAgregar))
);

layout.setVerticalGroup(
    layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(lblIdentificacion)
            .addComponent(txtIdentificacion))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(lblNombre)
            .addComponent(txtNombre))
        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(lblCorreo)
            .addComponent(txtCorreo))
        .addComponent(btnAgregar)
);

add(panelFormulario, BorderLayout.NORTH);

        // Agregar panel de formulario al lado izquierdo
        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.add(panelFormulario, BorderLayout.NORTH);
        add(panelIzquierdo, BorderLayout.WEST);

        // Panel de botones para gestión de archivos
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(3, 2, 10, 10));

        btnGuardarPlano = new JButton("Guardar Archivo Plano");
        btnLeerPlano = new JButton("Leer Archivo Plano");
        btnGuardarXML = new JButton("Guardar Archivo XML");
        btnLeerXML = new JButton("Leer Archivo XML");
        btnGuardarJSON = new JButton("Guardar Archivo JSON");
        btnLeerJSON = new JButton("Leer Archivo JSON");

        panelBotones.add(btnGuardarPlano);
        panelBotones.add(btnLeerPlano);
        panelBotones.add(btnGuardarXML);
        panelBotones.add(btnLeerXML);
        panelBotones.add(btnGuardarJSON);
        panelBotones.add(btnLeerJSON);

        // Barra inferior para botones
        add(panelBotones, BorderLayout.SOUTH);

        // Acciones de los botones
        btnAgregar.addActionListener(e -> agregarPersonaATabla());
        btnGuardarPlano.addActionListener(e -> guardarArchivoPlano());
        btnLeerPlano.addActionListener(e -> leerArchivoPlano());
        btnGuardarXML.addActionListener(e -> guardarArchivoXML());
        btnLeerXML.addActionListener(e -> leerArchivoXML());
        btnGuardarJSON.addActionListener(e -> guardarArchivoJSON());
        btnLeerJSON.addActionListener(e -> leerArchivoJSON());
    }

    // Métodos existentes (agregar, guardar, leer, etc.) permanecen igual
    private void agregarPersonaATabla() {
        String id = txtIdentificacion.getText();
        String nombre = txtNombre.getText();
        String correo = txtCorreo.getText();

        if (!id.isEmpty() && !nombre.isEmpty() && !correo.isEmpty()) {
            try {
                validarCorreo(correo); // Validar el correo
                modeloTabla.addRow(new Object[]{id, nombre, correo});
                limpiarFormulario();
            } catch (CorreoInvalidoException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        txtIdentificacion.setText("");
        txtNombre.setText("");
        txtCorreo.setText("");
    }

    private void guardarArchivoPlano() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("personas.txt"))) {
            int rowCount = modeloTabla.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                String id = modeloTabla.getValueAt(i, 0).toString();
                String nombre = modeloTabla.getValueAt(i, 1).toString();
                String correo = modeloTabla.getValueAt(i, 2).toString();
                writer.write(id + ";" + nombre + ";" + correo);
                writer.newLine();
            }
            JOptionPane.showMessageDialog(this, "Datos guardados en archivo plano correctamente.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void leerArchivoPlano() {
        try (BufferedReader reader = new BufferedReader(new FileReader("personas.txt"))) {
            String linea;
            modeloTabla.setRowCount(0); // Limpiar la tabla antes de cargar los datos
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length == 3) {
                    modeloTabla.addRow(new Object[]{datos[0], datos[1], datos[2]});
                }
            }
            JOptionPane.showMessageDialog(this, "Datos cargados desde el archivo plano correctamente.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarArchivoXML() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document doc = builder.newDocument();
            Element rootElement = doc.createElement("Personas");
            doc.appendChild(rootElement);

            int rowCount = modeloTabla.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                Element persona = doc.createElement("Persona");

                Element id = doc.createElement("ID");
                id.appendChild(doc.createTextNode(modeloTabla.getValueAt(i, 0).toString()));
                persona.appendChild(id);

                Element nombre = doc.createElement("Nombre");
                nombre.appendChild(doc.createTextNode(modeloTabla.getValueAt(i, 1).toString()));
                persona.appendChild(nombre);

                Element correo = doc.createElement("Correo");
                correo.appendChild(doc.createTextNode(modeloTabla.getValueAt(i, 2).toString()));
                persona.appendChild(correo);

                rootElement.appendChild(persona);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("personas.xml"));

            transformer.transform(source, result);

            JOptionPane.showMessageDialog(this, "Datos guardados en archivo XML correctamente.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar el archivo XML.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void leerArchivoXML() {
          try {
            File archivo = new File("personas.xml");
            if (!archivo.exists()) {
                throw new FileNotFoundException();
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(archivo);

            NodeList listaPersonas = doc.getElementsByTagName("Persona");
            modeloTabla.setRowCount(0); // Limpiar la tabla antes de cargar los datos

            for (int i = 0; i < listaPersonas.getLength(); i++) {
                Node nodo = listaPersonas.item(i);
                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) nodo;

                    String id = elemento.getElementsByTagName("ID").item(0).getTextContent();
                    String nombre = elemento.getElementsByTagName("Nombre").item(0).getTextContent();
                    String correo = elemento.getElementsByTagName("Correo").item(0).getTextContent();

                    modeloTabla.addRow(new Object[]{id, nombre, correo});
                }
            }
            JOptionPane.showMessageDialog(this, "Datos cargados desde el archivo XML correctamente.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo XML.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarArchivoJSON() {
        List<Persona> personas = new ArrayList<>();
        int rowCount = modeloTabla.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            String id = modeloTabla.getValueAt(i, 0).toString();
            String nombre = modeloTabla.getValueAt(i, 1).toString();
            String correo = modeloTabla.getValueAt(i, 2).toString();
            personas.add(new Persona(id, nombre, correo));
            }

        try (Writer writer = new FileWriter("personas.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create(); // Configurar Pretty Printing
            gson.toJson(personas, writer);
            JOptionPane.showMessageDialog(this, "Datos guardados en archivo JSON correctamente con formato.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar el archivo JSON.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void leerArchivoJSON() {
        try (Reader reader = new FileReader("personas.json")) {
            Gson gson = new Gson();
            Persona[] personasArray = gson.fromJson(reader, Persona[].class); // Leer como un array de Persona

            modeloTabla.setRowCount(0); // Limpiar la tabla antes de cargar los datos
            for (Persona persona : personasArray) {
                modeloTabla.addRow(new Object[]{persona.getIdentificacion(), persona.getNombre(), persona.getCorreo()});
            }
            JOptionPane.showMessageDialog(this, "Datos cargados desde el archivo JSON correctamente.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al leer el archivo JSON.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void validarCorreo(String correo) throws CorreoInvalidoException {
        if (!correo.contains("@") || !correo.endsWith(".com")) {
            throw new CorreoInvalidoException("El correo ingresado es inválido. Debe contener '@' y terminar en '.com'.");
        }
    }

    public class CorreoInvalidoException extends Exception {
        public CorreoInvalidoException(String mensaje) {
            super(mensaje);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Ventana ventana = new Ventana();
            ventana.setVisible(true);
        });
    }
}