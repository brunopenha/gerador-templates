
package br.nom.penha.bruno.gerador_template;

import java.util.Calendar;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.nom.penha.bruno.gerador_template.entidade.Address;
import br.nom.penha.bruno.gerador_template.entidade.City;
import br.nom.penha.bruno.gerador_template.entidade.Client;
import br.nom.penha.bruno.gerador_template.entidade.Neighborhood;

public class ClientTemplate {


    public void load() {
        Fixture.of(Client.class).addTemplate("valido", new Rule(){{
         add("address", one(Address.class, "valido"));
         add("birthday", (Calendar) instant("18 years ago"));
         add("birthdayAsString", "valor");
         
         add("email", "valor");
         add("id",  random(Long.class, range(1L, 200L)));
         add("name", "valor");
         add("nickname", "valor");
         add("tipoPrimitivo", (int) (int) 1);
        }});
        Fixture.of(Address.class).addTemplate("valido", new Rule(){{
         add("city", one(City.class, "valido"));
         
         add("country", "valor");
         add("id", random(Long.class, range(1L, 200L)));
         add("state", "valor");
         add("street", "valor");
         add("zipCode", "valor");
        }});
        Fixture.of(City.class).addTemplate("valido", new Rule(){{
         
         add("name", "valor");
         add("neighborhoods", has(3).of(Neighborhood.class, "valido", "valido", "valido"));
        }});
    }

}
