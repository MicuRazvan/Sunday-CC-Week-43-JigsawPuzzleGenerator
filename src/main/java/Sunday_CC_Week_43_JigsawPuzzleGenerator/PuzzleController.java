package Sunday_CC_Week_43_JigsawPuzzleGenerator;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Controller
public class PuzzleController {

    private static final int GAME_BOARD_SIZE = 480;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/generate")
    public String generatePuzzle(
            @RequestParam("file") MultipartFile file,
            @RequestParam("pieces") int pieces,
            Model model) {

        if (file.isEmpty()) {
            model.addAttribute("error", "Please upload a valid image file.");
            return "index";
        }

        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                model.addAttribute("error", "The uploaded file is not a supported image format.");
                return "index";
            }

            BufferedImage resizedOriginal = resizeImage(originalImage, GAME_BOARD_SIZE, GAME_BOARD_SIZE);
            String referenceBase64 = encodeToBase64(resizedOriginal);

            int cols;
            int rows;
            if (pieces == 64) {
                cols = 8;
                rows = 8;
            } else if (pieces == 128) {
                cols = 16;
                rows = 8;
            } else {
                cols = 4;
                rows = 4;
            }

            int pieceWidth = GAME_BOARD_SIZE / cols;
            int pieceHeight = GAME_BOARD_SIZE / rows;

            List<PuzzlePiece> puzzlePieces = new ArrayList<>();
            int id = 0;

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    BufferedImage subImage = resizedOriginal.getSubimage(
                            col * pieceWidth,
                            row * pieceHeight,
                            pieceWidth,
                            pieceHeight
                    );

                    String base64Piece = encodeToBase64(subImage);
                    int targetX = col * pieceWidth;
                    int targetY = row * pieceHeight;

                    puzzlePieces.add(new PuzzlePiece(id++, row, col, targetX, targetY, base64Piece));
                }
            }

            model.addAttribute("pieces", puzzlePieces);
            model.addAttribute("referenceImage", referenceBase64);
            model.addAttribute("pieceWidth", pieceWidth);
            model.addAttribute("pieceHeight", pieceHeight);
            model.addAttribute("totalPieces", pieces);

        } catch (IOException e) {
            model.addAttribute("error", "An error occurred while processing the image: " + e.getMessage());
            return "index";
        }

        return "puzzle";
    }

    private BufferedImage resizeImage(BufferedImage original, int width, int height) {
        Image scaled = original.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage buffered = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = buffered.createGraphics();
        g2d.drawImage(scaled, 0, 0, null);
        g2d.dispose();
        return buffered;
    }

    private String encodeToBase64(BufferedImage img) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        byte[] bytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);
    }
}