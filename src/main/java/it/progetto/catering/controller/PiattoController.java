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

import it.progetto.catering.controller.validator.PiattoValidator;
import it.progetto.catering.model.Piatto;
import it.progetto.catering.service.IngredienteService;
import it.progetto.catering.service.PiattoService;

@Controller
public class PiattoController {

	@Autowired
	private PiattoService piattoService;
	
	@Autowired
	private IngredienteService ingredienteService;

	@Autowired
	private PiattoValidator piattoValidator;	

	@PostMapping("/piatto")
	public String addPiatto(@Valid @ModelAttribute("piatto") Piatto piatto, BindingResult bindingResult, Model model) {
		piattoValidator.validate(piatto, bindingResult);//se lo chef che cerco di inserire e gia presente annullo l'inserimento, bindingResult da l'errore
		//prima di salvare l'ogg. persona dobbiamo verificare che non ci siano stati errori di validazione
		if(!bindingResult.hasErrors()) {//se non ci sono stati err di validazione
			piattoService.save(piatto);
			model.addAttribute("piatto", piatto);
			return "piatto.html";
		}
		else {
			model.addAttribute("ingredientsList", ingredienteService.findAll());
			return "piattoForm.html";  //altrimenti ritorna alla pagina della form
		}
	}

	@GetMapping("/piatto/{id}")
	public String getPiatto(@PathVariable("id") Long id, Model model) {
		Piatto piatto = piattoService.findById(id);
		model.addAttribute("piatto", piatto);//la stringa mi indica che nelle viste, per recuperare l'ogg lo chiamiamo persona
		model.addAttribute("dishIngredientsList", piatto.getIngredienti());
		return "piatto.html"; //la vista successiva mostra i dettagli della persona
	}

	@GetMapping("/dishes")
	public String getAllPiatti(Model model) {
		List<Piatto> piatti = piattoService.findAll();
		model.addAttribute("piatti", piatti);
		return "piatti.html";
	}
	
	@GetMapping("/editPiattoForm/{id}")
	public String getPiattoForm(@PathVariable Long id, Model model) {
		model.addAttribute("piatto", piattoService.findById(id));
		return "editDishForm.html";
	}

	@Transactional
	@PostMapping("/edit/piatto/{id}")
	public String modificaPiatto(@PathVariable Long id, @Valid @ModelAttribute("piatto") Piatto piatto, BindingResult bindingResults, Model model) {
		if(!bindingResults.hasErrors()) {
			Piatto vecchioPiatto = piattoService.findById(id);
			vecchioPiatto.setNome(piatto.getNome());
			vecchioPiatto.setDescrizione(piatto.getDescrizione());
			this.piattoService.save(vecchioPiatto);
			model.addAttribute("piatto", piatto);
			return "piatto.html";
		} 
		else {
			model.addAttribute("listaIngredienti", ingredienteService.findAll());
			return "modificaPiattoForm.html";
		}
	}	

	@PostMapping("/remove/ask/piatto/{id}")
	public String askRemovePiattoById(@PathVariable("id") Long id, Model model) {
		model.addAttribute("piatto", piattoService.findById(id));
		return"piattoConfirmDelete.html";
	}

	@PostMapping("/remove/confirm/piatto/{id}")
	public String confirmRemovePiattoById(@PathVariable("id") Long id, Model model) {
		try {
			piattoService.deletePiattoById(id);
		} catch (Exception e){
			return "error.html";
		}
		return "success.html";
	}
	
	
	
	
	
	
	
	
	
	
	
	

}
