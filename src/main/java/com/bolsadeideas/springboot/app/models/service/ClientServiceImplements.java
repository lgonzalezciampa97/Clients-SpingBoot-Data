package com.bolsadeideas.springboot.app.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Transactional;

import com.bolsadeideas.springboot.app.models.dao.IClientDAO;
import com.bolsadeideas.springboot.app.models.entity.Client;

//Implements the Facade design pattern
@Service
public class ClientServiceImplements implements IClientService{

	@Autowired
	private IClientDAO clientDAO;
	
	@Override
	@Transactional(readOnly = true)
	public List<Client> findAll() {
		return (List<Client>) clientDAO.findAll();
	}

	@Override
	@Transactional
	public void save(Client client) {
		clientDAO.save(client);
	}

	@Override
	@Transactional(readOnly = true)
	public Client findOne(Long id) {
		return clientDAO.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		clientDAO.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Client> findAll(Pageable pageable) {
		
		return clientDAO.findAll(pageable);
	}

	@Override
	public void deleteAll() {
		clientDAO.deleteAll();
	}

	@Override
	public boolean isAnyClient() {
		if(clientDAO.count() == 0) {
			return false;
		}
		else {
			return true;
		}
		
	}
	
}
