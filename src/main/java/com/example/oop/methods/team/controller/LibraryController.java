package com.example.oop.methods.team.controller;

import com.example.oop.methods.team.model.Author;
import com.example.oop.methods.team.model.Book;
import com.example.oop.methods.team.model.Library;
import com.example.oop.methods.team.model.User;
import com.example.oop.methods.team.repository.AuthorRepository;
import com.example.oop.methods.team.repository.BookRepository;
import com.example.oop.methods.team.repository.LibraryRepository;
import com.example.oop.methods.team.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LibraryController {

    private UserRepository userRepository;
    private LibraryRepository libraryRepository;
    private BookRepository bookRepository;
    private AuthorRepository authorRepository;

    @Autowired
    public LibraryController(UserRepository userRepository, LibraryRepository libraryRepository, BookRepository bookRepository, AuthorRepository authorRepository) {
        this.userRepository = userRepository;
        this.libraryRepository = libraryRepository;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    @RequestMapping(path = "/library/display/{owner_id}", method = RequestMethod.GET)
    public String displayPersonalLibrary(
            @PathVariable(name = "owner_id")
            Long ownerId,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            Library library = this.libraryRepository.getLibraryByOwnerId(ownerId);
            if (library == null) {
                throw new Exception("No library specified for this user");
            }
            model.addAttribute("library", library);
            return "display_library_page";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/error";
        }
    }

    @RequestMapping(path = "/library/edit/{owner_id}", method = RequestMethod.GET)
    public String editPersonalLibrary(
            @PathVariable(name = "owner_id")
            Long ownerId,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            Library library = this.libraryRepository.getLibraryByOwnerId(ownerId);
            if (library == null) {
                throw new Exception("No library specified for this user");
            }
            model.addAttribute("library", library);
            return "edit_library_page";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/error";
        }
    }

    @RequestMapping(path = "/library/edit/{library_id}/remove/{book_id}", method = RequestMethod.GET)
    public String removeBookFromPersonalLibrary(
            @PathVariable(name = "library_id")
            Long libraryId,
            @PathVariable(name = "book_id")
            Long bookId,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            Library library = this.libraryRepository.getLibraryById(libraryId);
            Book book = this.bookRepository.getBookById(bookId);
            Author author = book.getAuthor();
            if (library == null) {
                throw new Exception("No library specified for this user");
            }
            this.libraryRepository.removeBookFromLibrary(book, library);
            Long authorId = author.getId();
            if (this.libraryRepository.getBooksByAuthorId(libraryId, authorId).isEmpty()) {
                this.libraryRepository.removeAuthorFromLibrary(author, library);
            }
            redirectAttributes.addFlashAttribute("library", library);
            return "redirect:/library/edit/" + library.getOwner().getId();
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/error";
        }
    }

    @RequestMapping(path = "/library/display/global/{owner_id}")
    public String viewGlobalLibrary(
            @PathVariable(name = "owner_id")
            Long ownerId,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            Library library = new Library();
            library.setBooks(this.bookRepository.getAllBooks());
            library.setAuthors(this.authorRepository.getAllAuthors());
            model.addAttribute("library", library);
            Library userLibrary = this.libraryRepository.getLibraryByOwnerId(ownerId);
            model.addAttribute("personalLibraryId", userLibrary.getId());
            model.addAttribute("userLibrary", userLibrary);
            return "display_global_library_page";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/error";
        }
    }

    @RequestMapping(path = "/library/edit/{library_id}/add/{book_id}")
    public String addBookToPersonalLibrary(
            @PathVariable(name = "library_id") Long libraryId,
            @PathVariable(name = "book_id") Long bookId,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Library library = this.libraryRepository.getLibraryById(libraryId);
            Book book = this.bookRepository.getBookById(bookId);
            Author author = book.getAuthor();
            if (library == null) {
                throw new Exception("No library specified for this user");
            }
            Long authorId = author.getId();
            if (this.libraryRepository.getBooksByAuthorId(libraryId, authorId).isEmpty()) {
                this.libraryRepository.addAuthorToLibrary(author, library);
            }
            this.libraryRepository.addBookToLibrary(book, library);
            redirectAttributes.addFlashAttribute("library", library);
            redirectAttributes.addFlashAttribute("savedBookId", bookId);
            redirectAttributes.addFlashAttribute("message", "Successfully saved");
            return "redirect:/library/display/global/" + library.getOwner().getId();
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/error";
        }
    }

    @RequestMapping(path = "/library/edit/global", method = RequestMethod.GET)
    public String editGlobalLibrary(Model model) {
        return "edit_global_library_page";
    }

}
