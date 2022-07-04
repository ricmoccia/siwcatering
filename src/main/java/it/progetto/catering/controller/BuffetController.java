package it.progetto.catering.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.progetto.catering.controller.validator.BuffetValidator;
import it.progetto.catering.model.Buffet;
import it.progetto.catering.service.BuffetService;
import it.progetto.catering.service.ChefService;
import it.progetto.catering.service.PiattoService;

@Controller
public class BuffetController {

	@Autowired
	private BuffetService buffetService;

	@Autowired
	private PiattoService piattoService;

	@Autowired
	private ChefService chefService;

	@Autowired
	private BuffetValidator buffetValidator;	

	@PostMapping("/buffet")
	public String addBuffet(@Valid @ModelAttribute("buffet") Buffet buffet, BindingResult bindingResult, Model model) {
		buffetValidator.validate(buffet, bindingResult);//se il buffet che cerco di inserire e gia presente annullo l'inserimento, bindingResult da l'errore
		//prima di salvare l'ogg. buffet dobbiamo verificare che non ci siano stati errori di validazione
		if(!bindingResult.hasErrors()) {//se non ci sono stati err di validazione
			buffetService.save(buffet);
			model.addAttribute("buffet", buffet);
			return "buffet.html";
		}
		else {
			model.addAttribute("dishesList", piattoService.findAll());
			model.addAttribute("chefsList", chefService.findAll());
			return "buffetForm.html";  //altrimenti ritorna alla pagina della form
		}		
	}

	@GetMapping("/buffet/{id}")
	public String getBuffet(@PathVariable("id") Long id, Model model) {
		Buffet buffet = buffetService.findById(id);
		model.addAttribute("buffet", buffet);//la stringa mi indica che nelle viste, per recuperare l'ogg lo chiamiamo persona
		return "buffet.html"; //la vista successiva mostra i dettagli della persona
	}

	@GetMapping("/buffets")
	public String getAllBuffets(Model model) {
		List<Buffet> buffets = buffetService.findAll();
		model.addAttribute("buffets", buffets);
		return "buffets.html";
	}

	@GetMapping("/modificaBuffetForm/{id}")
	public String getBuffetForm(@PathVariable Long id, Model model) {
		model.addAttribute("buffet", buffetService.findById(id));
		model.addAttribute("chefsList", chefService.findAll());
		model.addAttribute("dishesList", piattoService.findAll());
		return "editBuffetForm.html";
	}

	@Transactional
	@PostMapping("/edit/buffet/{id}")
	public String editBuffet(@PathVariable Long id, @Valid @ModelAttribute("buffet") Buffet buffet, BindingResult bindingResults, Model model) {
		if(!bindingResults.hasErrors()) {//se non ci sono errori di validazione
			Buffet oldBuffet = buffetService.findById(id);
			oldBuffet.setNome(buffet.getNome());
			oldBuffet.setDescrizione(buffet.getDescrizione());
			oldBuffet.setChef(buffet.getChef());
			oldBuffet.setPiatti(buffet.getPiatti());
			this.buffetService.save(oldBuffet);
			model.addAttribute("buffet", buffet);
			return "buffet.html"; //pagina con buffet appena modificato
		} 
		else {
			model.addAttribute("dishesList", piattoService.findAll());
			model.addAttribute("chefsList", chefService.findAll());
			return "editBuffetForm.html"; // ci sono errori, torna alla form iniziale
		}
	}	
	
	@PostMapping("/remove/ask/buffet/{id}")
	public String askRemoveBuffetById(@PathVariable("id") Long id, Model model) {
		model.addAttribute("buffet", this.buffetService.findById(id));
		String nextPage = "buffetConfirmDelete.html";
		return nextPage;
	}

	@PostMapping("/remove/confirm/buffet/{id}")
	public String confirmRemoveBuffetById(@PathVariable("id") Long id, Model model) {
		this.buffetService.deleteBuffetById(id);
		String nextPage = "success.html";
		return nextPage;
	}











}
