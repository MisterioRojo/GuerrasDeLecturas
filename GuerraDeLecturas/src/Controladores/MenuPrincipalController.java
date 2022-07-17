package Controladores;

/**
 * Programa que permite el acceso a una base de datos de comics. Mediante JDBC con mySql
 * Las ventanas graficas se realizan con JavaFX.
 * El programa permite:
 *  - Conectarse a la base de datos.
 *  - Ver la base de datos completa o parcial segun parametros introducidos.
 *  - Guardar el contenido de la base de datos en un fichero .txt y .xlsx,CSV
 *  - Copia de seguridad de la base de datos en formato .sql
 *  - Introducir comics a la base de datos.
 *  - Modificar comics de la base de datos.
 *  - Eliminar comics de la base de datos(Solamente cambia el estado de "En posesion" a "Vendido". Los datos siguen en la bbdd pero estos no los muestran el programa
 *  - Ver frases de personajes de comics
 *  - Opcion de escoger algo para leer de forma aleatoria.
 *
 *  Esta clase permite acceder al menu principal donde se puede viajar a diferentes ventanas, etc.
 *
 *  Version Final
 *
 *  Por Alejandro Rodriguez
 *
 *  Twitter: @silverAlox
 */

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import Funcionamiento.BBDD;
import Funcionamiento.Comic;
import Funcionamiento.ConexionBBDD;
import Funcionamiento.Libreria;
import Funcionamiento.NavegacionVentanas;
import Funcionamiento.Utilidades;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MenuPrincipalController {


	@FXML
	private Button botonLimpiar;

	@FXML
	private Button botonMostrarParametro;

	@FXML
	private Button botonSalir;

	@FXML
	private Button botonVolver;

	@FXML
	private Button botonbbdd;

	@FXML
	private Button BotonVentanaAniadir;

	@FXML
	private Button botonBackupBBDD;

	@FXML
	private Button botonFrase;

	@FXML
	private Button botonImportarCSV;

	@FXML
	private Button botonGuardarCSV;

	@FXML
	private Button botonDelete;

	@FXML
	private Button botonEstadistica;

	@FXML
	private Button botonCompra;

	@FXML
	private TextField anioPublicacion;

	@FXML
	private TextField numeroID;

	@FXML
	private TextField nombreComic;

	@FXML
	private TextField nombreEditorial;

	@FXML
	private TextField nombreFormato;

	@FXML
	private TextField nombreGuionista;

	@FXML
	private TextField nombreProcedencia;

	@FXML
	private TextField nombreVariante;

	@FXML
	private TextField numeroComic;

	@FXML
	private TableColumn<Comic, String> dibujante;

	@FXML
	private TableColumn<Comic, String> editorial;

	@FXML
	private TableColumn<Comic, String> fecha;

	@FXML
	private TableColumn<Comic, String> firma;

	@FXML
	private TableColumn<Comic, String> formato;

	@FXML
	private TableColumn<Comic, String> guionista;

	@FXML
	private TableColumn<Comic, String> nombre;

	@FXML
	private TableColumn<Comic, String> ID;

	@FXML
	private TableColumn<Comic, String> numero;

	@FXML
	private TableColumn<Comic, String> procedencia;

	@FXML
	private TableColumn<Comic, String> variante;

	@FXML
	public TableView<Comic> tablaBBDD;

	@FXML
	private TextArea prontInfo;

	@FXML
	private TextArea prontFrases;

	private NavegacionVentanas nav = new NavegacionVentanas();

	private Libreria libreria = new Libreria();

	private BBDD db = new BBDD();

	private Connection conn = ConexionBBDD.conexion();

	/////////////////////////////////
	//// METODOS LLAMADA A VENTANAS//
	/////////////////////////////////

	/**
	 * Permite abrir y cargar la ventana para IntroducirDatosController
	 *
	 * @param event
	 */
	@FXML
	public void ventanaAniadir(ActionEvent event) {

		nav.verIntroducirDatos();

		Stage myStage = (Stage) this.BotonVentanaAniadir.getScene().getWindow();
		myStage.close();
	}


	/**
	 * Muestra en un textArea diferentes frases random de personajes de los comics.
	 * @param event
	 */
	@FXML
	void fraseRandom(ActionEvent event) {
		prontFrases.setOpacity(1);
		prontFrases.setText(Comic.frasesComics());
	}

	/**
	 * Muestra la bbdd segun los parametros introducidos en los TextField
	 *
	 * @param event
	 */
	@FXML
	void mostrarPorParametro(ActionEvent event) {
		nombreColumnas();
		listaPorParametro();
	}

	/**
	 * Muestra toda la base de datos.
	 *
	 * @param event
	 */
	@FXML
	void verTodabbdd(ActionEvent event) {
		nombreColumnas();
		tablaBBDD(libreriaPosesion());
	}

	////////////////////////////
	/// METODOS PARA EXPORTAR///
	////////////////////////////

	/**
	 * Importa un fichero CSV compatible con el programa para copiar la informacion a la base de datos
	 * @param event
	 */
	@FXML
	void importCSV(ActionEvent event) {

		FileChooser fileChooser = new FileChooser(); //Permite escoger donde se encuentra el fichero
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Fichero CSV", "*.csv")); //Permite escoger solamente ficheros cuya extension es CSV
		File fichero = fileChooser.showOpenDialog(null); //Hace que el fileChooser sea solamente para abrir el fichero

		importCSV(fichero);

	}

	/**
	 * Exporta un fichero CSV compatible con el programa que copia el contenido de la base de datos en un fichero CSV
	 * @param event
	 */
	@FXML
	void exportCSV(ActionEvent event) {

		FileChooser fileChooser = new FileChooser(); //Permite escoger donde se encuentra el fichero
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Fichero Excel xlsx", "*.xlsx"));  //Permite escoger solamente ficheros cuya extension es .xlsx
		File fichero = fileChooser.showSaveDialog(null);  //Hace que el fileChooser sea solamente para guardar el fichero

		makeExcel(fichero);
	}

	/**
	 * Exporta la base de datos en un fichero SQL
	 * @param event
	 */
	@FXML
	void exportarSQL(ActionEvent event) {

		FileChooser fileChooser = new FileChooser(); //Permite escoger donde se encuentra el fichero
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Fichero SQL", "*.sql")); //Permite escoger solamente ficheros cuya extension es .xlsx
		File fichero = fileChooser.showSaveDialog(null); //Hace que el fileChooser sea solamente para guardar el fichero

		makeSQL(fichero);

	}

	/**
	 * Limpia los campos de pantalla donde se escriben los datos.
	 *
	 * @param event
	 */
	@FXML
	void limpiarDatos(ActionEvent event) {

		numeroID.setText("");
		nombreComic.setText("");
		numeroComic.setText("");
		nombreVariante.setText("");
		nombreEditorial.setText("");
		nombreFormato.setText("");
		procedencia.setText("");
		anioPublicacion.setText("");
		nombreGuionista.setText("");
		prontInfo.setText(null);
		prontFrases.setText(null);
		prontInfo.setOpacity(0);
		prontFrases.setOpacity(0);
	}

	/**
	 * Borra el contenido de la base de datos, soltamente el contenido de las tablas.
	 * @param event
	 */
	@FXML
	void borrarContenidoTabla(ActionEvent event) {

		if (db.borrarContenidoTabla()) {
			prontInfo.setOpacity(1);
			prontInfo.setStyle("-fx-background-color: #A0F52D");
			prontInfo.setText("Has borrado correctamente el contenido de la base de datos.");
			tablaBBDD.getItems().clear();
		} else {
			prontInfo.setOpacity(1);
			prontInfo.setStyle("-fx-background-color: #F53636");
			prontInfo.setText("Has cancelado el borrado de la base de datos.");
		}
	}

	/**
	 * Se llama a funcion que permite ver las estadisticas de la bbdd
	 * @param event
	 */
	@FXML
	void verEstadistica(ActionEvent event) {
		procedimientosEstadistica();
	}

	/**
	 * Se llama a funcion que permite abrir 2 direccionesd web junto al navegador predeterminado
	 * @param event
	 */
	@FXML
	void comprarComic(ActionEvent event) {
		verPagina();
	}

	/////////////////////////////////
	//// FUNCIONES////////////////////
	/////////////////////////////////

	/**
	 * Funcion que permite llamar al navegador predeterminado del sistema y abrir 2 paginas web.
	 */
	public void verPagina()
	{
		String url1 = "https://www.radarcomics.com/es/";
		String url2 = "https://www.panini.es/shp_esp_es/comics.html";

		if (Utilidades.isWindows()) {
			accesoCompraWindows(url1); //Llamada a funcion
			accesoCompraWindows(url2); //Llamada a funcion
		} else {
			if (Utilidades.isUnix()) {
				accesoCompraLinux(url1); //Llamada a funcion
				accesoCompraLinux(url2); //Llamada a funcion
			} else {
				// No creada funcion para mac
			}
		}
	}

	/**
	 * Permite abrir las paginas web siempre que el sistema opertativo sea Linux
	 * @param url
	 */
	public void accesoCompraLinux(String url) {
		Runtime rt = Runtime.getRuntime();
		StringBuffer cmd = navegador(url); //Llamada a funcion
		
		try {
			rt.exec(new String[] { "sh", "-c", cmd.toString() });
		} catch (IOException e) {
			nav.alertaException("Error: No funciona el boton \n" + e.toString());
		}
	}

	/**
	 * Funcion que permite comprobar que navegadores tienes instalados en el sistema operativo linux y abre aquel que tengas en predeterminado.
	 * @param url
	 * @return
	 */
	public StringBuffer navegador(String url) {
		String[] browsers = { "google-chrome", "firefox", "mozilla", "epiphany", "konqueror", "netscape", "opera",
				"links", "lynx" };

		StringBuffer cmd = new StringBuffer();
		for (int i = 0; i < browsers.length; i++) {
			if (i == 0) {
				cmd.append(String.format("%s \"%s\"", browsers[i], url));
			} else {
				cmd.append(String.format(" || %s \"%s\"", browsers[i], url));
			}
		}
		return cmd;
	}

	/**
	 * Funcion que abre navegador predeterminado junto a una web siempre que el sistema operativo sea windows
	 * @param url
	 */
	public void accesoCompraWindows(String url) {
		try {
			java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
		} catch (IOException e) {
			nav.alertaException("Error: No funciona el boton \n" + e.toString());
		}
	}

	/**
	 * Funcion que llamada a los procedimientos almacenados en la base de datos y muestra diferentes datos.
	 */
	public void procedimientosEstadistica() {
		String procedimiento1;
		String procedimiento2;
		String procedimiento3;
		String procedimiento4;
		String procedimiento5;
		int numGrapas, numTomos, numUsa, numEsp, total;

		procedimiento1 = "call numeroGrapas()";
		procedimiento2 = "call numeroTomos()";
		procedimiento3 = "call numeroSpain()";
		procedimiento4 = "call numeroUSA()";
		procedimiento5 = "call total()";

		try {
			Statement st1 = conn.createStatement();
			Statement st2 = conn.createStatement();
			Statement st3 = conn.createStatement();
			Statement st4 = conn.createStatement();
			Statement st5 = conn.createStatement();

			ResultSet rs1 = st1.executeQuery(procedimiento1); //Executa el procedimiento almacenado
			ResultSet rs2 = st2.executeQuery(procedimiento2); //Executa el procedimiento almacenado
			ResultSet rs3 = st3.executeQuery(procedimiento3); //Executa el procedimiento almacenado
			ResultSet rs4 = st4.executeQuery(procedimiento4); //Executa el procedimiento almacenado
			ResultSet rs5 = st5.executeQuery(procedimiento5); //Executa el procedimiento almacenado

			//Si no hay dato que comprobar, devolvera un 0
			if (rs1.next()) {
				numGrapas = rs1.getInt(1);
			} else {
				numGrapas = 0;
			}
			if (rs2.next()) {
				numTomos = rs2.getInt(1);
			} else {
				numTomos = 0;
			}
			if (rs3.next()) {
				numEsp = rs3.getInt(1);
			} else {
				numEsp = 0;
			}
			if (rs4.next()) {
				numUsa = rs4.getInt(1);
			} else {
				numUsa = 0;
			}
			if (rs5.next()) {
				total = rs5.getInt(1);
			} else {
				total = 0;
			}

			prontInfo.setOpacity(1);
			prontInfo.setText("Numero de grapas: " + numGrapas + "\nNumero de tomos: " + numTomos
					+ "\nNumeros de comics en Castellano: " + numEsp + "\nNumero de comics en USA: " + numUsa + "\nTotal: "
					+ total);

			rs1.close();
			rs2.close();
			rs3.close();
			rs4.close();
			rs5.close();

		} catch (SQLException e) {
			nav.alertaException(e.toString());
		}
	}

	/**
	 * Permite dar valor a las celdas de la TableView
	 */
	private void nombreColumnas() {
		ID.setCellValueFactory(new PropertyValueFactory<>("ID"));
		nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
		numero.setCellValueFactory(new PropertyValueFactory<>("numero"));
		variante.setCellValueFactory(new PropertyValueFactory<>("variante"));
		editorial.setCellValueFactory(new PropertyValueFactory<>("editorial"));
		formato.setCellValueFactory(new PropertyValueFactory<>("formato"));
		procedencia.setCellValueFactory(new PropertyValueFactory<>("procedencia"));
		fecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
		guionista.setCellValueFactory(new PropertyValueFactory<>("guionista"));
	}

	/////////////////////////////////
	//// FUNCIONES CREACION FICHEROS//
	/////////////////////////////////

	/**
	 * Funcion que compruba si se ha creado el fichero Excel y CSV
	 * @param fichero
	 */
	public void makeExcel(File fichero) {
		try {

			if (fichero != null) {
				if (db.crearExcel(fichero)) { //Si el fichero XLSX y CSV se han creado se vera el siguiente mensaje
					prontInfo.setOpacity(1);
					prontInfo.setStyle("-fx-background-color: #A0F52D");
					prontInfo.setText("Fichero excel exportado de forma correcta");
				} else { //Si no se ha podido crear correctamente los ficheros se vera el siguiente mensaje
					prontInfo.setOpacity(1);
					prontInfo.setStyle("-fx-background-color: #F53636");
					prontInfo.setText("ERROR. No se ha podido exportar correctamente.");
				}
			} else { //En caso de cancelar la creacion de los ficheros, se mostrara el siguiente mensaje.
				prontInfo.setOpacity(1);
				prontInfo.setStyle("-fx-background-color: #F53636");
				prontInfo.setText("ERROR. Se ha cancelado la exportacion.");
			}
		} catch (Exception e) {
			nav.alertaException(e.toString());
		}
	}

	/**
	 * Funcion que compruba si se ha importado el fichero  CSV
	 * @param fichero
	 */
	public void importCSV(File fichero) {
		try {

			if (fichero != null) {
				if (db.importarCSV(fichero)) { //Si se ha importado el fichero CSV correctamente, se vera el siguiente mensaje
					prontInfo.setOpacity(1);
					prontInfo.setStyle("-fx-background-color: #A0F52D");
					prontInfo.setText("Fichero CSV importado de forma correcta");
				} else { //Si no se ha podido crear importar el fichero se vera el siguiente mensaje
					prontInfo.setOpacity(1);
					prontInfo.setStyle("-fx-background-color: #F53636");
					prontInfo.setText("ERROR. No se ha podido importar correctamente.");
				}
			} else { //En caso de cancelar la importacion del fichero, se mostrara el siguiente mensaje.
				prontInfo.setOpacity(1);
				prontInfo.setStyle("-fx-background-color: #F53636");
				prontInfo.setText("ERROR. Se ha cancelado la importacion.");
			}
		} catch (Exception e) {
			nav.alertaException(e.toString());
		}
	}

	/**
	 * Funcion crea el fichero SQL segun el sistema operativo en el que te encuentres.
	 * @param fichero
	 */
	public void makeSQL(File fichero) {
		if (fichero != null) {

			if (Utilidades.isWindows()) {
				db.backupWindows(fichero); //Llamada a funcion
				prontInfo.setOpacity(1);
				prontInfo.setStyle("-fx-background-color: #A0F52D");
				prontInfo.setText("Base de datos exportada \ncorrectamente");

			} else {
				if (Utilidades.isUnix()) {
					db.backupLinux(fichero); //Llamada a funcion
					prontInfo.setOpacity(1);
					prontInfo.setStyle("-fx-background-color: #A0F52D");
					prontInfo.setText("Base de datos exportada \ncorrectamente");
				} 
			}
		}
		else
		{
			prontInfo.setOpacity(1);
			prontInfo.setStyle("-fx-background-color: #F53636");
			prontInfo.setText("ERROR. Se ha cancelado la exportacion de la base de datos.");
		}
	}


	/**
	 *
	 * @throws SQLException
	 */
	public void listaPorParametro() {
		String datosComic[] = camposComic();

		Comic comic = new Comic(datosComic[0], datosComic[1], datosComic[2], datosComic[3], datosComic[4],
				datosComic[5], datosComic[6], datosComic[7], datosComic[8], datosComic[9], datosComic[10], "");

		tablaBBDD(libreriaParametro(comic));
	}

	/**
	 * Devuelve una lista de los comics cuyos datos han sido introducidos mediante parametros en los textField
	 * @param comic
	 * @return
	 */
	public List<Comic> libreriaParametro(Comic comic) {
		List<Comic> listComic = FXCollections.observableArrayList(libreria.filtadroBBDD(comic));

		if (listComic.size() == 0) {
			prontInfo.setStyle("-fx-background-color: #F53636");
			prontInfo.setText("ERROR. No hay ningun dato en la base de datos");
		}
		return listComic;

	}

	/**
	 * Devuelve una lista con todos los comics de la base de datos que se encuentran "En posesion"
	 * @return
	 */
	public List<Comic> libreriaPosesion() {
		List<Comic> listComic = FXCollections.observableArrayList(libreria.verLibreria());

		if (listComic.size() == 0) {
			prontInfo.setStyle("-fx-background-color: #F53636");
			prontInfo.setText("ERROR. No hay ningun dato en la base de datos");
		}

		return listComic;
	}

	/** 
	 * Devuelve una lista con todos los comics de la base de datos.
	 * @return
	 */
	public List<Comic> libreriaCompleta() {
		List<Comic> listComic = FXCollections.observableArrayList(libreria.verLibreriaCompleta());

		if (listComic.size() == 0) {
			prontInfo.setStyle("-fx-background-color: #F53636");
			prontInfo.setText("ERROR. No hay ningun dato en la base de datos");
		}

		return listComic;
	}

	/**
	 * Obtiene los datos de los comics de la base de datos y los devuelve en el textView
	 * @param listaComic
	 */
	@SuppressWarnings("unchecked")
	public void tablaBBDD(List<Comic> listaComic) {
		tablaBBDD.getColumns().setAll(ID, nombre, numero, variante, firma, editorial, formato, procedencia, fecha,
				guionista, dibujante);
		tablaBBDD.getItems().setAll(listaComic);
	}

	/**
	 * Devuelve un array con los datos de los TextField correspondientes a la los comics que se encuentran en la bbdd
	 * @return
	 */
	public String[] camposComic() {
		String campos[] = new String[11];

		campos[0] = numeroID.getText();

		campos[1] = nombreComic.getText();

		campos[2] = numeroComic.getText();

		campos[3] = nombreVariante.getText();

		campos[5] = nombreEditorial.getText();

		campos[6] = nombreFormato.getText();

		campos[7] = nombreProcedencia.getText();

		campos[8] = anioPublicacion.getText();

		campos[9] = nombreGuionista.getText();

		return campos;
	}

	/////////////////////////////
	//// FUNCIONES PARA SALIR////
	/////////////////////////////

	/**
	 * Vuelve al menu inicial de conexion de la base de datos.
	 *
	 * @param event
	 * @throws IOException
	 */
	@FXML
	public void volverMenu(ActionEvent event) throws IOException {

		nav.verAccesoBBDD();
		ConexionBBDD.close();
		Stage myStage = (Stage) this.botonVolver.getScene().getWindow();
		myStage.close();
	}

	/**
	 * Permite salir completamente del programa.
	 *
	 * @param event
	 */
	@FXML
	public void salirPrograma(ActionEvent event) {

		if (nav.salirPrograma(event)) {
			Stage myStage = (Stage) this.botonSalir.getScene().getWindow();
			myStage.close();
		}
	}

	/**
	 * Al cerrar la ventana, carga la ventana del menu principal
	 *
	 * @throws IOException
	 */
	public void closeWindows() {

		nav.verAccesoBBDD();

		Stage myStage = (Stage) this.botonVolver.getScene().getWindow();
		myStage.close();

	}
}
