package com.bolsadeideas.springboot.app.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
//import java.net.http.HttpHeaders;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
//import org.springframework.data.querydsl.QPageRequest;
//import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//import com.bolsadeideas.springboot.app.models.dao.IClientDAO;
import com.bolsadeideas.springboot.app.models.entity.Client;
import com.bolsadeideas.springboot.app.models.service.IClientService;
import com.bolsadeideas.springboot.app.models.service.IUploadFileService;
import com.bolsadeideas.springboot.app.util.paginator.PageRender;
//import com.bolsadeideas.springboot.app.models.service.IClientService;

@Controller
@SessionAttributes("client")
public class ClientController {

	@Autowired
	private IClientService clientService;

	@Autowired
	private IUploadFileService uploadFileService;

	@GetMapping(value = "/uploads/{filename:.+}")
	public ResponseEntity<Resource> seePhoto(@PathVariable String filename) throws MalformedURLException {

		Resource resource = uploadFileService.load(filename);

		return ResponseEntity.ok().header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
	}

	@GetMapping(value = "/see/{id}")
	public String see(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {

		Client client = clientService.findOne(id);
		if (client == null) {
			flash.addFlashAttribute("error", "The Client doesn´t exists on DB");
			return "redirect:/listar";
		}
		model.put("client", client);
		model.put("title", "Client detail: " + client.getName());

		return "see";
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String doList(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {

		boolean isAnyClient = clientService.isAnyClient();
		
		Pageable pageRequest = PageRequest.of(page, 4);

		Page<Client> clients = clientService.findAll(pageRequest);

		PageRender<Client> pageRender = new PageRender<>("/list", clients);

		model.addAttribute("isAnyClient",isAnyClient);
		model.addAttribute("title", "Client List");
		model.addAttribute("clients", clients);
		model.addAttribute("page", pageRender);
		
		return "list";
	}

	@RequestMapping(value = "/form", method = RequestMethod.GET)
	public String create(Map<String, Object> model) {

		Client client = new Client();
		model.put("client", client);
		model.put("title", "Client Form");
		model.put("btn", "Create Client");
		if(client.getPhoto() == null) {
			client.setPhoto("");
		}

		return "form";
	}

	@RequestMapping(value = "/form/{id}")
	public String edit(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {
		Client client = null;

		if (id > 0) {
			client = clientService.findOne(id);
			if (client == null) {
				flash.addFlashAttribute("error", "ID not found on DB");
				return "redirect:/list";
			}
		} else {
			flash.addFlashAttribute("error", "ID can´t be zero");
			return "redirect:/list";
		}

		model.put("client", client);
		model.put("title", "Edit Client");
		model.put("btn", "Edit Client");

		return "form";
	}

	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String save(@Valid Client client, BindingResult result, Model model,
			@RequestParam("file") MultipartFile photo, RedirectAttributes flash, SessionStatus status)
			throws IOException {

		if (result.hasErrors()) {
			model.addAttribute("title", "Client Form");
			model.addAttribute("btn", "Create Client");
			return "form";
		}

		if (!photo.isEmpty()) {

			if (client.getId() != null && client.getId() > 0 && client.getPhoto() != null
					&& client.getPhoto().length() > 0) {
				uploadFileService.delete(client.getPhoto());
			}

			String uniqueFileName = uploadFileService.copy(photo);
			flash.addFlashAttribute("success", "Photo Uploaded '" + uniqueFileName + "'");
			client.setPhoto(uniqueFileName);
		}

		String flashMessage = (client.getId() != null) ? "¡Client Edited!" : "¡Client Saved!";

		clientService.save(client);
		status.setComplete();
		flash.addFlashAttribute("success", flashMessage);

		return "redirect:/list";
	}

	@RequestMapping(value = "/delete/{id}")
	public String delete(@PathVariable(value = "id") Long id, RedirectAttributes flash) {

		if (id > 0) {
			Client client = clientService.findOne(id);

			clientService.delete(id);
			flash.addFlashAttribute("success", "¡Client deleted!");

			if (uploadFileService.delete(client.getPhoto())) {
				flash.addFlashAttribute("info", "Photo " + client.getPhoto() + " deleted!");
			}
		}

		return "redirect:/list";
	}
	
	@RequestMapping(value = "/deleteall")
	public String deleteAll(RedirectAttributes flash) {
		
		clientService.deleteAll();
		flash.addFlashAttribute("success", "¡All Clients Deleted!");

		return "redirect:/list";
	}

}
