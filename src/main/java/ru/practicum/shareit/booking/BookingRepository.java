package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "order by b.start desc")
    List<Booking> findAllByBookerId(Long bookerId);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "and CURRENT_TIMESTAMP between b.start and b.end " +
            "order by b.start desc")
    List<Booking> findCurrentBooking(Long bookerId);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "and CURRENT_TIMESTAMP > b.end " +
            "order by b.start desc")
    List<Booking> findPastBooking(Long bookerId);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 " +
            "and CURRENT_TIMESTAMP < b.start " +
            "order by b.start desc")
    List<Booking> findFutureBooking(Long bookerId);

    @Query("select b " +
            "from Booking b " +
            "where b.booker.id = ?1 and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> findBookingByStatus(Long bookerId, BookingStatus bookingStatus);

    @Query("select b " +
            "from Booking b " +
            "join b.item i " +
            "where i.owner.id = ?1 " +
            "order by b.start desc")
    List<Booking> findBookingByItemsOwner(Long ownerId);

    @Query("select b " +
            "from Booking b " +
            "join b.item i " +
            "where i.owner.id = ?1 " +
            "and CURRENT_TIMESTAMP between b.start and b.end " +
            "order by b.start desc")
    List<Booking> findCurrentBookingByItemsOwner(Long ownerId);

    @Query("select b " +
            "from Booking b " +
            "join b.item i " +
            "where i.owner.id = ?1 " +
            "and CURRENT_TIMESTAMP > b.end " +
            "order by b.start desc")
    List<Booking> findPastBookingByItemsOwner(Long ownerId);

    @Query("select b " +
            "from Booking b " +
            "join b.item i " +
            "where i.owner.id = ?1 " +
            "and CURRENT_TIMESTAMP < b.start " +
            "order by b.start desc")
    List<Booking> findFutureBookingByItemsOwner(Long ownerId);

    @Query("select b " +
            "from Booking b " +
            "join b.item i " +
            "where i.owner.id = ?1 and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> findBookingByOwnerIdAndStatus(Long ownerId, BookingStatus bookingStatus);

    @Query("select b " +
            "from Booking b " +
            "where b.item.id = ?1 " +
            "and b.status = 'APPROVED' " +
            "and (CURRENT_TIMESTAMP > b.end " +
            "or CURRENT_TIMESTAMP between b.start and b.end) " +
            "order by b.end desc")
    List<Booking> findLastBookingByItemId(Long itemId);

    @Query("select b " +
            "from Booking b " +
            "where b.item.id = ?1 " +
            "and b.status = 'APPROVED' " +
            "and CURRENT_TIMESTAMP < b.start " +
            "order by b.start")
    List<Booking> findNextBookingByItemId(Long itemId);

    @Query("select b " +
            "from Booking b " +
            "where b.item.id in :items " +
            "and b.status = 'APPROVED' " +
            "and (CURRENT_TIMESTAMP > b.end " +
            "or CURRENT_TIMESTAMP between b.start and b.end) " +
            "order by b.end desc")
    List<Booking> findLastBookingByItemIn(List<Long> items);

    @Query("select b " +
            "from Booking b " +
            "where b.item.id in :items " +
            "and b.status = 'APPROVED' " +
            "and CURRENT_TIMESTAMP < b.start " +
            "order by b.start")
    List<Booking> findNextBookingByItemIn(List<Long> items);
}
