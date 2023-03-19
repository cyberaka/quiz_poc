package com.cyberaka.quiz;

import com.lowagie.text.pdf.BaseFont;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Abhinav on 9/20/17.
 */
@Component
public class PdfGenaratorUtil {

    private Logger log = Logger.getLogger(getClass().getName());

    @Autowired
    private TemplateEngine templateEngine;

    public void createPdf(String templateName, String fontFile, String fileName, Map map) throws Exception {
        log.info("createPdf(" + templateName + ", " + fontFile + ", " + fileName + ", " + map + ")");
        Assert.notNull(templateName, "The templateName can not be null");
        Context ctx = new Context();
        if (map != null) {
            Iterator itMap = map.entrySet().iterator();
            while (itMap.hasNext()) {
                Map.Entry pair = (Map.Entry) itMap.next();
                ctx.setVariable(pair.getKey().toString(), pair.getValue());
            }
        }

        String processedHtml = templateEngine.process(templateName, ctx);
//        Files.write(Paths.get(fileName + ".html"), processedHtml.getBytes());
        FileOutputStream os = null;
        try {
            final File outputFile = new File(fileName);
            os = new FileOutputStream(outputFile);

            ITextRenderer renderer = new ITextRenderer();
            ITextFontResolver resolver = renderer.getFontResolver();
            resolver.addFont(fontFile, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            renderer.setDocumentFromString(processedHtml);
            renderer.layout();
            renderer.createPDF(os, false);
            renderer.finishPDF();
            log.info("PDF created successfully >> " + fileName);
        }
        finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) { /*ignore*/ }
            }
        }
    }
}
