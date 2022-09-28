package com.bolsadeideas.springboot.app.models.dao;

//import java.util.List;

//import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.bolsadeideas.springboot.app.models.entity.Client;

public interface IClientDAO extends PagingAndSortingRepository<Client, Long>{
	

}
