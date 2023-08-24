package edu.ucsb.cs156.happiercows.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ucsb.cs156.happiercows.ControllerTestCase;
import edu.ucsb.cs156.happiercows.entities.Commons;
import edu.ucsb.cs156.happiercows.entities.Profit;
import edu.ucsb.cs156.happiercows.entities.User;
import edu.ucsb.cs156.happiercows.entities.UserCommons;
import edu.ucsb.cs156.happiercows.repositories.CommonsRepository;
import edu.ucsb.cs156.happiercows.repositories.ProfitRepository;
import edu.ucsb.cs156.happiercows.repositories.UserCommonsRepository;
import edu.ucsb.cs156.happiercows.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProfitsController.class)
@Import(ProfitsController.class)
@AutoConfigureDataJpa
public class ProfitsControllerTests extends ControllerTestCase {
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    ProfitRepository profitRepository;

    @MockBean
    UserCommonsRepository userCommonsRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    CommonsRepository commonsRepository;

    User user = User.builder().id(1).build();
    Commons commons = Commons.builder().id(1).build();
    UserCommons uc1 = UserCommons.builder().user(user)
            .commons(commons).build();

    LocalDateTime t1 = LocalDateTime.parse("2022-03-05T15:50:10");

    Profit p1 = Profit.builder().id(41).amount(123.45).timestamp(t1).userCommons(uc1).numCows(1).avgCowHealth(80).build();
    Profit p2 = Profit.builder().id(42).amount(246.90).timestamp(t1).userCommons(uc1).numCows(2).avgCowHealth(80).build();
    Profit p3 = Profit.builder().id(43).amount(370.35).timestamp(t1).userCommons(uc1).numCows(3).avgCowHealth(80).build();
    Profit p4 = Profit.builder().id(43).amount(493.80).timestamp(t1).userCommons(uc1).numCows(3).avgCowHealth(80).build();

    List<Profit> profits = List.of(p1);
    List<Profit> allProfits = List.of(p1, p2, p3, p4);

    @WithMockUser(roles = {"USER"})
    @Test
    public void get_profits_all_commons_using_commons_id() throws Exception {
        UserCommons expectedUserCommons = p1.getUserCommons();
        when(profitRepository.findAllByUserCommons(uc1)).thenReturn(profits);
        when(userCommonsRepository.findByCommonsIdAndUserId(2L, 1L)).thenReturn(Optional.of(expectedUserCommons));

        MvcResult response = mockMvc.perform(get("/api/profits/all/commonsid?commonsId=2")).andDo(print())
                .andExpect(status().isOk()).andReturn();

        verify(profitRepository, times(1)).findAllByUserCommons(uc1);

        String responseString = response.getResponse().getContentAsString();
        List<Profit> actualProfits = objectMapper.readValue(responseString, new TypeReference<List<Profit>>() {
        });

        // json serialized result doesn't include userCommons.user or userCommons.commons,
        // so we exclude them from expected
        p1.getUserCommons().setUser(null);
        p1.getUserCommons().setCommons(null);

        assertEquals(profits, actualProfits);
    }

    @WithMockUser(roles = {"USER"})
    @Test
    public void get_profits_all_commons_nonexistent_using_commons_id() throws Exception {
        MvcResult response = mockMvc.perform(get("/api/profits/all/commonsid?commonsId=2").contentType("application/json"))
                .andExpect(status().isNotFound()).andReturn();

        verify(userCommonsRepository, times(1)).findByCommonsIdAndUserId(2L, 1L);

        Map<String, Object> json = responseToJson(response);
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals("UserCommons with commonsId 2 and userId 1 not found",
                json.get("message"));
    }

    @WithMockUser(roles = {"USER"})
    @Test
    public void get_paged_profits_commons_using_commons_id() throws Exception {
        UserCommons expectedUserCommons = p1.getUserCommons();

        when(userCommonsRepository.findByCommonsIdAndUserId(2L, 1L)).thenReturn(Optional.of(expectedUserCommons));

        Page<Profit> pagedProfits = new PageImpl<>(profits);
        when(profitRepository.findAllByUserCommons(eq(uc1))).thenReturn(pagedProfits);

        MvcResult response = mockMvc.perform(get("/api/profits/paged/commonsid?commonsId=2&page=0&size=5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andReturn();

        String responseString = response.getResponse().getContentAsString();
        JsonNode jsonResponse = objectMapper.readTree(responseString);

        assertEquals(1, jsonResponse.get("totalElements").asInt());
        assertEquals(0, jsonResponse.get("number").asInt());
        assertEquals(5, jsonResponse.get("size").asInt());
        assertEquals(1, jsonResponse.get("totalPages").asInt());
        assertEquals(p1.getAmount(), jsonResponse.get("content").get(0).get("amount").asDouble(), 0.01);
    }

    @WithMockUser(roles = {"USER"})
    @Test
    public void get_paged_profits_all_commons_using_commons_id() throws Exception {
        UserCommons expectedUserCommons = p1.getUserCommons();

        when(userCommonsRepository.findByCommonsIdAndUserId(2L, 1L)).thenReturn(Optional.of(expectedUserCommons));

        Page<Profit> pagedProfits = new PageImpl<>(allProfits);
        when(profitRepository.findAllByUserCommons(eq(uc1))).thenReturn(pagedProfits);

        MvcResult response = mockMvc.perform(get("/api/profits/paged/commonsid?commonsId=2&page=1&size=2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andReturn();

        String responseString = response.getResponse().getContentAsString();
        JsonNode jsonResponse = objectMapper.readTree(responseString);

        assertEquals(4, jsonResponse.get("totalElements").asInt());
        assertEquals(2, jsonResponse.get("totalPages").asInt());
        assertEquals(p4.getAmount(), jsonResponse.get("content").get(1).get("amount").asDouble(), 0.01);
    }


    @WithMockUser(roles = {"USER"})
    @Test
    public void get_paged_profits_all_commons_nonexistent_using_commons_id_with() throws Exception {
        MvcResult response = mockMvc.perform(get("/api/profits/paged/commonsid?commonsId=2").contentType("application/json"))
                .andExpect(status().isNotFound()).andReturn();

        verify(userCommonsRepository, times(1)).findByCommonsIdAndUserId(2L, 1L);

        Map<String, Object> json = responseToJson(response);
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals("UserCommons with commonsId 2 and userId 1 not found",
                json.get("message"));
    }

}
