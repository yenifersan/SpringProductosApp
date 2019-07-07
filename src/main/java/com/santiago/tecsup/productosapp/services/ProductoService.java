package com.santiago.tecsup.productosapp.services;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.santiago.tecsup.productosapp.models.Producto;
import com.santiago.tecsup.productosapp.repositories.ProductoRepository;




@Service
public class ProductoService {

	@Autowired
	private ProductoRepository productoRepository;
	
	public List<Producto> listar(){
		return productoRepository.listar();
	}
	

	public void crear(Producto producto) {
		productoRepository.crear(producto);
	}
	public void eliminar(Integer id) {
		productoRepository.eliminar(id);
	}
	

	
}
