package course.cashcard.controllers;

import course.cashcard.models.CashCardModel;
import course.cashcard.requests.CashCardRequest;
import course.cashcard.interfaces.CurrentOwner;
import course.cashcard.repositories.CashCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/cashcards")
class CashCardController {
    @Autowired
    private CashCardRepository repo;

    private CashCardModel findCashCard(Long requestedId, Principal principal) {
        return repo.findByIdAndOwner(requestedId, principal.getName());
    }

    @PostAuthorize("returnObject.body.owner == authentication.name")
    @GetMapping("/{requestedId}")
    public ResponseEntity<CashCardModel> findById(@PathVariable Long requestedId) {
        return repo.findById(requestedId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CashCardModel> createCashCard(
            @RequestBody CashCardRequest cashCardRequest,
            UriComponentsBuilder ucb,
            @CurrentOwner String owner
    ) {
        CashCardModel cashCard = new CashCardModel();
        cashCard.setAmount(cashCardRequest.amount());
        cashCard.setOwner(owner);
        CashCardModel savedCashCard = repo.save(cashCard);
        URI locationOfNewCashCard = ucb
                .path("cashcards/{id}")
                .buildAndExpand(savedCashCard.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewCashCard).body(savedCashCard);
    }

    @GetMapping
    private ResponseEntity<List<CashCardModel>> findAll(Pageable pageable, Principal principal) {
        Page<CashCardModel> page = repo.findByOwner(
                principal.getName(),
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
                ));
        return ResponseEntity.ok(page.getContent());
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putCashCard(@PathVariable Long requestedId, @RequestBody CashCardModel cashCardUpdate, Principal principal) {
        CashCardModel cashCard = findCashCard(requestedId, principal);
        if (cashCard == null){
            throw new NoSuchElementException("Cash card not found");
            //return ResponseEntity.notFound().build();
        }
        CashCardModel updatedCashCard = new CashCardModel(cashCard.getId(), cashCardUpdate.getAmount(), principal.getName());
        repo.save(updatedCashCard);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteCashCard(@PathVariable Long id, Principal principal) {
        if (repo.existsByIdAndOwner(id, principal.getName())) {
            repo.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}