package course.cashcard;

import course.cashcard.models.CashCardModel;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@TestPropertySource("classpath:application-test.properties")
class CashCardJsonTest {

    @Autowired
    private JacksonTester<CashCardModel> json;

    @Autowired
    private JacksonTester<CashCardModel[]> jsonList;

    private CashCardModel[] cashCards;

    @BeforeEach
    void setUp() {
        cashCards = Arrays.array(
                new CashCardModel(99L, 123.45, "sarah1"),
                new CashCardModel(100L, 1.00, "sarah1"),
                new CashCardModel(101L, 150.00, "sarah1"));
    }

    @Test
    void cashCardSerializationTest() throws IOException {
        CashCardModel cashCard = cashCards[0];
        assertThat(json.write(cashCard)).isStrictlyEqualToJson("../../single.json");
        assertThat(json.write(cashCard)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(cashCard)).extractingJsonPathNumberValue("@.id")
                .isEqualTo(99);
        assertThat(json.write(cashCard)).hasJsonPathNumberValue("@.amount");
        assertThat(json.write(cashCard)).extractingJsonPathNumberValue("@.amount")
                .isEqualTo(123.45);
    }

    @Test
    void cashCardDeserializationTest() throws IOException {
        String expected = """
                {
                    "id": 99,
                    "amount": 123.45,
                    "owner": "sarah1"
                }
                """;
        assertThat(json.parseObject(expected).getId()).isEqualTo(99);
        assertThat(json.parseObject(expected).getAmount()).isEqualTo(123.45);
    }


    @Test
    void cashCardListSerializationTest() throws IOException {
        assertThat(jsonList.write(cashCards)).isStrictlyEqualToJson("../../list.json");
    }

    @Test
    void cashCardListDeserializationTest() throws IOException {
        String expected="""
            [
                { "id": 99, "amount": 123.45, "owner": "sarah1" },
                { "id": 100, "amount": 1.00, "owner": "sarah1" },
                { "id": 101, "amount": 150.00, "owner": "sarah1" }
            ]
            """;
        CashCardModel[] deserializedCashCards = jsonList.parse(expected).getObject();
        assertThat(deserializedCashCards).hasSize(cashCards.length);
        for (int i = 0; i < cashCards.length; i++) {
            assertThat(deserializedCashCards[i].getId()).isEqualTo(cashCards[i].getId());
            assertThat(deserializedCashCards[i].getAmount()).isEqualTo(cashCards[i].getAmount());
        }
    }
}
