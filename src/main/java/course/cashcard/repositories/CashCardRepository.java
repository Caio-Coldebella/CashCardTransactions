package course.cashcard.repositories;

import course.cashcard.models.CashCardModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashCardRepository extends JpaRepository<CashCardModel,
        Long>, PagingAndSortingRepository<CashCardModel, Long> {
    CashCardModel findByIdAndOwner(Long id, String owner);
    Page<CashCardModel> findByOwner(String owner, PageRequest pageRequest);
    boolean existsByIdAndOwner(Long id, String owner);

    //@Query("select * from cash_card cc where cc.owner = :#{authentication.name}")
    //Page<CashCardModel> findAll(PageRequest pageRequest);
}
