package easymytrip.example.easymytrip;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class login {
    @GetMapping("/login")
    public String getData() {return  "Please book your hotel ticket in bengluru  40% discount" ; }
}
