package io.mtakeshi1.pdfunite

import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.pdf.PdfWriter
import org.springframework.context.annotation.Scope
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.io.ByteArrayOutputStream

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
class FileUploadController {

    var fileList: MutableList<Pair<String, ByteArray>> = mutableListOf()

    @PostMapping("/clear")
    fun clear() {
        fileList.clear()
    }

    @PostMapping("/generate")
    fun generate(): ResponseEntity<Resource> {
        if (fileList.isEmpty()) return ResponseEntity.noContent().build();
        val bout = ByteArrayOutputStream()
        val doc = Document()
        PdfWriter.getInstance(doc, bout)
        doc.open()
        for ((_, data) in fileList) {
            doc.newPage()
            val img = Image.getInstance(data)
            img.scaleToFit(doc.pageSize)
            doc.add(img)
        }
        doc.close()
        fileList.clear()
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, """attachment; filename="generated.pdf"""")
            .body(ByteArrayResource(bout.toByteArray()))
    }

    @GetMapping("/image/:name")
    fun getImage() {

    }

    @GetMapping("/")
    fun listUploadedFiles(model: Model): String {
        model.addAttribute("files", fileList.map { it.first })
        return "uploadForm"
    }

    @PostMapping
    fun uploadFile(file: MultipartFile, redirect: RedirectAttributes): String {
        if (file.originalFilename?.endsWith(".png", true) != true &&
            file.originalFilename?.endsWith(".jpg", true) != true &&
            file.originalFilename?.endsWith(".jpeg", true) != true &&
            file.originalFilename?.endsWith(".gif", true) != true
        ) {
            redirect.addFlashAttribute("message", "invalid file =(")
        } else {
            fileList.add(file.originalFilename!! to file.bytes)
            redirect.addFlashAttribute("message", "upload successfull")
        }
        return "redirect:/"
    }

}