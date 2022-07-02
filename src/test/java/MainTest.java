import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    List<String> listOfInitialStrings = List.of(
            "hjkh(h(k)jh{hjkhkjh}hioh)jlj[kjgkjg]kljlkj(kljlkj){}",
            "hjkh(h(k)jh{hjkh(kjh}hioh)jlj[kjgkjg]kljlkj(kljlkj){}",
            ")hjkh(h(k)jh{hjkhkjh}hioh)jlj[kjgkjg]kljlkj(kljlkj){}"

            );

    @Test
    public void test(){
        listOfInitialStrings.stream().forEach(args -> Main.main(new String[]{args}));
    }

}