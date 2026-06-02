package io.github.mrspock182.pokemon.resource;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api-pokemon/pokemon/v1")
public class BattleResource {

    @ResponseStatus(OK)
    @PutMapping(path = "/battle")
    public String battle() {
        return null;
    }

    @ResponseStatus(OK)
    @PutMapping(path = "/new-pokemon")
    public String newPokemon() {
        return null;
    }

}