package io.pillopl.library.lending.patron.model;

import io.pillopl.library.lending.book.model.BookId;
import io.pillopl.library.lending.book.model.BookOnHold;
import io.pillopl.library.lending.library.model.LibraryBranchId;
import io.pillopl.library.lending.patron.model.PatronInformation.PatronType;
import io.vavr.collection.List;

import java.util.*;

import static io.pillopl.library.lending.book.model.BookFixture.anyBookId;
import static io.pillopl.library.lending.library.model.LibraryBranchFixture.anyBranch;
import static io.pillopl.library.lending.patron.model.PatronInformation.PatronType.Regular;
import static io.pillopl.library.lending.patron.model.PatronInformation.PatronType.Researcher;
import static io.pillopl.library.lending.patron.model.PlacingOnHoldPolicy.*;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.rangeClosed;

public class PatronBooksFixture {

    public static PatronBooks regularPatron() {
        return regularPatron(anyPatronId());
    }

    public static PatronBooks regularPatronWithPolicy(PlacingOnHoldPolicy placingOnHoldPolicy) {
        return patronWithPolicy(anyPatronId(), Regular, placingOnHoldPolicy);
    }

    public static PatronBooks researcherPatronWithPolicy(PlacingOnHoldPolicy placingOnHoldPolicy) {
        return patronWithPolicy(anyPatronId(), Researcher, placingOnHoldPolicy);
    }

    public static PatronBooks regularPatronWithPolicy(PatronId patronId, PlacingOnHoldPolicy placingOnHoldPolicy) {
        return patronWithPolicy(patronId, Regular, placingOnHoldPolicy);
    }

    public static PatronBooks researcherPatronWithPolicy(PatronId patronId, PlacingOnHoldPolicy placingOnHoldPolicy) {
        return patronWithPolicy(patronId, Researcher, placingOnHoldPolicy);
    }

    private static PatronBooks patronWithPolicy(PatronId patronId, PatronType type, PlacingOnHoldPolicy placingOnHoldPolicy) {
        return new PatronBooks(patronInformation(patronId, type),
                List.of(placingOnHoldPolicy),
                new OverdueCheckouts(new HashMap<>()),
                noHolds());
    }

    public static PatronBooks regularPatron(PatronId patronId) {
        return new PatronBooks(
                patronInformation(patronId, Regular),
                List.of(onlyResearcherPatronsCanHoldRestrictedBooksPolicy),
                new OverdueCheckouts(new HashMap<>()),
                noHolds());
    }

    public static PatronBooks researcherPatron(PatronId patronId) {
        return new PatronBooks(
                patronInformation(patronId, Researcher),
                List.of(onlyResearcherPatronsCanHoldRestrictedBooksPolicy),
                new OverdueCheckouts(new HashMap<>()),
                noHolds());
    }

    static PatronInformation patronInformation(PatronId id, PatronType type) {
        return new PatronInformation(id, type);
    }

    public static PatronBooks regularPatronWithHolds(int numberOfHolds) {
        PatronId patronId = anyPatronId();
        return new PatronBooks(
                patronInformation(patronId, Regular),
                List.of(regularPatronMaximumNumberOfHoldsPolicy),
                new OverdueCheckouts(new HashMap<>()),
                booksOnHold(numberOfHolds));
    }

    static PatronBooks regularPatronWith(Hold hold) {
        PatronId patronId = anyPatronId();
        PatronHolds patronHolds = new PatronHolds(Collections.singleton(hold));
        return new PatronBooks(
                patronInformation(patronId, Regular),
                allCurrentPolicies(),
                new OverdueCheckouts(new HashMap<>()),
                patronHolds);
    }

    public static PatronBooks regularPatronWith(BookOnHold bookOnHold, PatronId patronId) {
        PatronHolds patronHolds = new PatronHolds(Collections.singleton(new Hold(bookOnHold.getBookId(), bookOnHold.getHoldPlacedAt())));
        return new PatronBooks(
                patronInformation(patronId, Regular),
                allCurrentPolicies(),
                new OverdueCheckouts(new HashMap<>()),
                patronHolds);
    }

    public static Hold onHold() {
        return new Hold(anyBookId(), anyBranch());
    }

    static PatronHolds booksOnHold(int numberOfHolds) {
        return new PatronHolds(rangeClosed(1, numberOfHolds)
                .mapToObj(i -> new Hold(anyBookId(), anyBranch()))
                .collect(toSet()));
    }

    static PatronBooks researcherPatronWithHolds(int numberOfHolds) {
        PatronId patronId = anyPatronId();
        return new PatronBooks(
                patronInformation(patronId, Researcher),
                List.of(regularPatronMaximumNumberOfHoldsPolicy),
                new OverdueCheckouts(new HashMap<>()),
                booksOnHold(numberOfHolds));
    }

    static PatronBooks regularPatronWithOverdueCheckouts(LibraryBranchId libraryBranchId, Set<BookId> overdueBooks) {
        Map<LibraryBranchId, Set<OverdueCheckout>> overdueCheckouts = new HashMap<>();
        overdueCheckouts.put(libraryBranchId, createOverdueCheckouts(overdueBooks));
        return new PatronBooks(
                patronInformation(anyPatronId(), Regular),
                List.of(overdueCheckoutsRejectionPolicy),
                new OverdueCheckouts(overdueCheckouts),
                noHolds());
    }

    private static Set<OverdueCheckout> createOverdueCheckouts(Set<BookId> overdueBooks) {
        return overdueBooks
                .stream()
                .map(OverdueCheckout::new)
                .collect(toSet());
    }

    static PatronBooks regularPatronWith3_OverdueCheckoutsAt(LibraryBranchId libraryBranchId) {
        Map<LibraryBranchId, Set<OverdueCheckout>> overdueCheckouts = new HashMap<>();
        Set<BookId> overdueBooks = Set.of(anyBookId(), anyBookId(), anyBookId());
        overdueCheckouts.put(libraryBranchId, createOverdueCheckouts(overdueBooks));
        return new PatronBooks(
                patronInformation(anyPatronId(), Regular),
                List.of(overdueCheckoutsRejectionPolicy),
                new OverdueCheckouts(overdueCheckouts),
                noHolds());
    }

    public static PatronId anyPatronId() {
        return patronId(UUID.randomUUID());
    }

    static PatronId patronId(UUID patronId) {
        return new PatronId(patronId);
    }

    static PatronHolds noHolds() {
        return new PatronHolds(new HashSet<>());
    }


    public static PatronBooks regularPatronWithHold(BookOnHold bookOnHold) {
        return regularPatronWith(new Hold(bookOnHold.getBookId(), bookOnHold.getHoldPlacedAt()));
    }


}