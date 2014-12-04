package edu.udima.es;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import edu.udima.es.dominio.Cliente;

/**
 * Apliacion cliente de escritorio de los servicios web ubicado
 * en https://integrasaas.herokuapp.com
 * Muestra un menu interactivo sobre las peticiones del recurso
 * cliente a modo de ejemplo.
 * 
 * Para probar todos los servicios se debe usar el cliente
 * de SOAPUI u otro cliente REST
 * 
 * @author Sofia Sabariego
 */
public class RestClient {
	
	/*
	 * URL base de los recursos,
	 */
	private static String DOMINIO = "https://integrasaas.herokuapp.com";


	/**
	 * Solicita una cantidad por pantalla y calcula el IVA.
	 * 
	 * @param args no acepta parametros
	 * @throws IOException producidas por fallos en la lectura o escritura
	 */
	public static void main(String[] args) throws IOException {
		Integer numeroConsulta;    

		numeroConsulta = Integer.parseInt(seleccionarConsulta());
		lanzarConsulta(numeroConsulta);
	}
	

	/*
	 * Elimina un cliente del que el usuario especifica el id
	 */
	private static void eliminarCliente() throws IOException {
		escribir("Seleccione el identificador (id) del cliente: ");
		String id = leer();
		StringBuilder path = new StringBuilder("clientes");
		path.append("/").append(id);

		Response deleteResponse = getRestClient().target(DOMINIO).path(path.toString()).
				request().accept(MediaType.APPLICATION_JSON).delete();
		escribir(deleteResponse.getStatusInfo().toString());
	}

	/*
	 * Modifica el cliente del que el usuario especifica su id
	 * y proporciona los nuevos datos.
	 */
	private static void modificarCliente() throws IOException {
		escribir("Seleccione el identificador (id) del cliente: ");
		String id = leer();
		StringBuilder path = new StringBuilder("clientes");
		path.append("/").append(id);

		Cliente clienteSelccionado = getRestClient().target(DOMINIO).path(path.toString()).
				request().accept(MediaType.APPLICATION_JSON).get(Cliente.class);

	
		escribir("Introduzca nombre del cliente: ");
		clienteSelccionado.setNombre(leer());
		escribir("Introduzca los apellidos del cliente: ");
		clienteSelccionado.setApellidos(leer());
		escribir("Introduzca direccion del cliente: ");
		clienteSelccionado.setDireccion(leer());
		escribir("Introduzca password del cliente: ");
		clienteSelccionado.setPassword(leer());
		
		Response postResponse = getRestClient().target(DOMINIO).path(path.toString()).
				request().put(Entity.entity(clienteSelccionado, MediaType.APPLICATION_JSON));
		
		escribir(postResponse.getStatusInfo().toString());
		
	}

	/*
	 * Crea un cliente con los datos que el usuario
	 * va introduciendo
	 */
	private static void crearCliente() throws IOException {
		Cliente cliente = new Cliente();
		escribir("Introduzca nombre del cliente: ");
		cliente.setNombre(leer());
		escribir("Introduzca los apellidos del cliente: ");
		cliente.setApellidos(leer());
		escribir("Introduzca DNI del cliente: ");
		cliente.setDni(leer());
		escribir("Introduzca direccion del cliente: ");
		cliente.setDireccion(leer());
		escribir("Introduzca password del cliente: ");
		cliente.setPassword(leer());
		
		Response postResponse = getRestClient().target(DOMINIO).path("clientes").
				request().post(Entity.entity(cliente, MediaType.APPLICATION_JSON));
		escribir(postResponse.getStatusInfo().toString());
	}

	/*
	 * Consulta los datos de un cliente del que el usuario especifica su id
	 * el formato de datos devuelto depende del mime type seleccionado
	 * por el usuario 
	 */
	private static void consultarCliente(String acceptedMime) throws IOException {
		escribir("Seleccione el identificador (id) del cliente: ");
		String id = leer();
		StringBuilder path = new StringBuilder("clientes");
		path.append("/").append(id);

		Response response = getRestClient().target(DOMINIO).path(path.toString()).
				request().accept(acceptedMime).get();

		escribir(response.readEntity(String.class));

	}

	/*
	 *  Consulta todos los clientes disponibles en el sistema integrasaas.
	 *  El formato de datos devuelto depende del mime type especificado por el usuario.
	 */
	private static void consultarClientes(String acceptedMime) {
		String responseEntity = getRestClient().target(DOMINIO).path("clientes").
				request().accept(acceptedMime).get(String.class);

		escribir(responseEntity);
	}
	
	/*
	 *  Crea un cliente rest especificando un TIME OUT ya que
	 *  el acceso a los recursos del servidor heroku tarda en resolverse.
	 */
	private static Client getRestClient() {
		ClientConfig configuration = new ClientConfig();
		configuration.property(ClientProperties.CONNECT_TIMEOUT, 10000);
		configuration.property(ClientProperties.READ_TIMEOUT, 10000);
		return ClientBuilder.newClient(configuration);
	}
	
	/*
	 * Metodo para seleccionar la consulta de entre las opciones del menu.
	 */
	private static String seleccionarConsulta() throws IOException {
		escribir("Las consultas que se pueden ejecutar son:\n");
		escribir("1.- Consulta todos los clientes formato JSON");
		escribir("2.- Consulta todos los clientes formato XML");
		escribir("3.- Consulta un cliente formato JSON (pide datos)");
		escribir("4.- Consulta un cliente formato XML (pide datos)");
		escribir("5.- Da de alta un cliente (pide datos)");
		escribir("6.- Modifica un cliente (pide datos)");
		escribir("7.- Eliminar un cliente (pide datos)");
		escribir("\n");
		escribir("Nº consulta:");
		return leer();
	}

	/*
	 * Ayuda para escribir tanto por consola como por la salida estandar
	 * dependiendo de el entorno de ejecucion.
	 */
	private static void escribir(String texto) {
		if(System.console() != null){           
			System.console().writer().println(texto);
		}else{
			System.out.println(texto);
		}
	}

	/*
	 * Ayuda para leer tanto de consola como de la entrada estandar
	 * dependiendo de el entorno de ejecucion.
	 */
	private static String leer() throws IOException {
		BufferedReader br;
		if(System.console() != null){           
			return System.console().readLine();
		}else{
			br = new BufferedReader(new InputStreamReader(System.in));
			return br.readLine();
		}
	}

	/*
	 *  Redirige a la consulta adecuada segun la opcion escogida.
	 * @throws IOException 
	 */
	private static void lanzarConsulta(Integer numeroConsulta) throws IOException {
		switch(numeroConsulta){
		case 1:{
			consultarClientes(MediaType.APPLICATION_JSON);
			break;                
		}
		case 2:{
			consultarClientes(MediaType.APPLICATION_XML);
			break;
		}
		case 3:{
			consultarCliente(MediaType.APPLICATION_JSON);
			break;
		}
		case 4:{
			consultarCliente(MediaType.APPLICATION_XML);
			break;
		}
		case 5:{
			crearCliente();
			break;
		}
		case 6:{
			modificarCliente();
			break;
		}
		case 7:{
			eliminarCliente();
			break;
		}
		default: escribir("Opcion incorrecta");
		}
	}



}
