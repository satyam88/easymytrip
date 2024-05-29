package easymytrip.example.easymytrip;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class bus {
    @GetMapping("/bus")
    public String getData() {return  "Please book your bus ticket in bengluru  49% discount" ; }
}
