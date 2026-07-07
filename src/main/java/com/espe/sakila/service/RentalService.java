package com.espe.sakila.service;

import com.espe.sakila.dto.RentalRequestDTO;
import com.espe.sakila.dto.RentalResponseDTO;
import com.espe.sakila.entity.Inventory;
import com.espe.sakila.entity.Rental;
import com.espe.sakila.entity.User;
import com.espe.sakila.exception.InsufficientStockException;
import com.espe.sakila.exception.InvalidOperationException;
import com.espe.sakila.exception.ResourceNotFoundException;
import com.espe.sakila.mapper.RentalMapper;
import com.espe.sakila.repository.InventoryRepository;
import com.espe.sakila.repository.RentalRepository;
import com.espe.sakila.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RentalMapper rentalMapper;

    /**
     * Registra una o varias rentas en un único bloque transaccional.
     * Si cualquier película no tiene stock, se hace rollback completo.
     */
    @Transactional
    public List<RentalResponseDTO> createRental(String username, RentalRequestDTO request) {
        User user = getUserOrThrow(username);
        List<Rental> created = new ArrayList<>();

        for (RentalRequestDTO.RentalItemDTO item : request.getItems()) {
            for (int i = 0; i < item.getQuantity(); i++) {
                // PESSIMISTIC_WRITE: previene doble reserva concurrente del mismo inventario
                Inventory inventory = inventoryRepository
                        .findFirstAvailableByFilmId(item.getFilmId())
                        .orElseThrow(() -> new InsufficientStockException(
                                "Stock insuficiente para la película con id: " + item.getFilmId()));

                Rental rental = new Rental();
                rental.setInventory(inventory);
                rental.setUser(user);
                rental.setRentalDate(OffsetDateTime.now());

                created.add(rentalRepository.save(rental));
            }
        }

        return created.stream().map(rentalMapper::toDTO).collect(Collectors.toList());
    }

    /**
     * Devuelve una película: solo el dueño del rental puede devolverla.
     */
    @Transactional
    public RentalResponseDTO returnRental(Integer rentalId, String username) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("Rental no encontrado con id: " + rentalId));

        if (!rental.getUser().getUsername().equals(username)) {
            throw new InvalidOperationException("No puedes devolver un rental que no te pertenece.");
        }
        if (rental.getReturnDate() != null) {
            throw new InvalidOperationException("Esta película ya fue devuelta.");
        }

        rental.setReturnDate(OffsetDateTime.now());
        return rentalMapper.toDTO(rentalRepository.save(rental));
    }

    @Transactional(readOnly = true)
    public List<RentalResponseDTO> getActiveRentals(String username) {
        User user = getUserOrThrow(username);
        return rentalRepository.findByUserUserIdAndReturnDateIsNull(user.getUserId())
                .stream().map(rentalMapper::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RentalResponseDTO> getRentalHistory(String username) {
        User user = getUserOrThrow(username);
        return rentalRepository.findByUserUserId(user.getUserId())
                .stream().map(rentalMapper::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RentalResponseDTO> getAllRentals() {
        return rentalRepository.findAll()
                .stream().map(rentalMapper::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RentalResponseDTO> getAllActiveRentals() {
        return rentalRepository.findByReturnDateIsNull()
                .stream().map(rentalMapper::toDTO).collect(Collectors.toList());
    }

    private User getUserOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
    }
}
