package com.santiago.tecsup.productosapp.controllers;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.santiago.tecsup.productosapp.models.Producto;
import com.santiago.tecsup.productosapp.models.ResponseMessage;
import com.santiago.tecsup.productosapp.services.ProductoService;

@RestController
public class ProductoController {

	private static final Logger logger = LoggerFactory.getLogger(HelloController.class);
	
	private static final String FILEPATH = "/var/data/productos-api/images";
	
	@Autowired
	private ProductoService productoService;
	
	@GetMapping("/productos")
	public List<Producto> productos() {
		logger.info("call productos");
		
		List<Producto> productos = productoService.listar();
		
		return productos;
	}
	
	@GetMapping("/productos/images/{filename:.+}")
	public ResponseEntity<Resource> files(@PathVariable String filename) throws Exception{
		logger.info("call images: " + filename);
		
		Resource resource = new UrlResource(Paths.get(FILEPATH).resolve(filename).toUri());
		logger.info("Resource: " + resource);
		
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\""+resource.getFilename()+"\"")
				.header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(Paths.get(FILEPATH).resolve(filename)))
				.header(HttpHeaders.CONTENT_LENGTH, String.valueOf(resource.contentLength()))
				.body(resource);
	}
	
	
	@PostMapping("/productos")	// https://spring.io/guides/gs/uploading-files/
	public ResponseMessage crear(@RequestParam(name="imagen", required=false) MultipartFile imagen, @RequestParam("nombre") String nombre, @RequestParam("precio") Double precio, @RequestParam("detalles") String detalles) {
		logger.info("call crear(" + nombre + ", " + precio + ", " + detalles + ", " + imagen + ")");
		
		Producto producto = new Producto();
		producto.setNombre(nombre);
		producto.setPrecio(precio);
		producto.setDetalles(detalles);
		
		if (imagen != null && !imagen.isEmpty()) {
			try {
				
				producto.setImagen(imagen.getOriginalFilename());
				
				Files.copy(imagen.getInputStream(), Paths.get(FILEPATH).resolve(imagen.getOriginalFilename()));
				
			}catch(IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		
		productoService.crear(producto);
		
		return ResponseMessage.success("Registro completo");
	}
	
	@DeleteMapping("/productos/{id}")
	public ResponseMessage eliminar(@PathVariable Integer id) {
		logger.info("call eliminar: " + id);
		
		productoService.eliminar(id);
		
		return ResponseMessage.success("Registro eliminado");
	}


	
}

