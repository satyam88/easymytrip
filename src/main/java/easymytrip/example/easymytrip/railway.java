package easymytrip.example.easymytrip;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class railway {
    @GetMapping("/railway")
    public String getData() {return  "Please book your train ticket on vandne bharat 25% discount" ; }
}