package educanet;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Square {

    private float[] vertices;

    private static final int[] indices = {
            0, 1, 2,
            1, 2, 3,
    };


    private int squareVaoId;
    private int squareVboId;
    private int squareEboId;
    private int colorsId;


    public static int uniform;
    public Matrix4f matrix;
    public FloatBuffer matrixFB;

    private float x;
    private float y;
    private final float width;


    private static int textureId;


    public Square(float x, float y, float width) {
        vertices = new float[12];
        squareVaoId = GL33.glGenVertexArrays();
        squareVboId = GL33.glGenBuffers();
        squareEboId = GL33.glGenBuffers();
        colorsId = GL33.glGenBuffers();


        this.x = x;
        this.y = y;
        this.width = width;

        matrix = new Matrix4f().identity();
        matrixFB = BufferUtils.createFloatBuffer(16);
        textureId = GL33.glGenTextures();


        loadImage();
        for (int i = 0; i < 4; i++) {
            vertices[i * 3] = x + width * (i % 2);
            vertices[i * 3 + 1] = y - width * (Math.round(i / 2));
            vertices[i * 3 + 2] = 0.0f;
        }


        float[] colors = {
                1f, 1f, 1f, 1f,
                1f, 1f, 1f, 1f,
                1f, 1f, 1f, 1f,
                1f, 1f, 1f, 1f,
        };

        uniform = GL33.glGetUniformLocation(Shaders.shaderProgramId, "matrix");

        //VERTICES
        GL33.glBindVertexArray(squareVaoId);
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, squareVboId);
        FloatBuffer fb = BufferUtils.createFloatBuffer(vertices.length)
                .put(vertices)
                .flip();
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, fb, GL33.GL_STATIC_DRAW);
        GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(0);


        // COLORS
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, colorsId);
        FloatBuffer cfb = BufferUtils.createFloatBuffer(colors.length).put(colors).flip();
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, cfb, GL33.GL_STATIC_DRAW);
        GL33.glVertexAttribPointer(1, 4, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(1);


        //EBO
        GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, squareEboId);
        IntBuffer ib = BufferUtils.createIntBuffer(indices.length)
                .put(indices)
                .flip();
        GL33.glBufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, ib, GL33.GL_STATIC_DRAW);

        //MATRIX
        GL33.glUseProgram(Shaders.shaderProgramId);
        matrix.get(matrixFB);
        GL33.glUniformMatrix4fv(uniform, false, matrixFB);
    }

    public void render() {
        GL33.glUseProgram(Shaders.shaderProgramId);

        // Draw using the glDrawElements function
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, textureId);
        GL33.glBindVertexArray(squareVaoId);
        GL33.glDrawElements(GL33.GL_TRIANGLES, indices.length, GL33.GL_UNSIGNED_INT, 0);
    }


    private float xSS = 0.008f;
    private float ySS = 0.008f;

    public void update(long window) {
        matrix = matrix.translate(xSS, ySS, 0f);

        x += xSS;
        y += ySS;

        if (x + width > 1 || x < -1) {
            xSS = ySS * -1;
        }
        if (y > 1 || y - width < -1) {
            ySS = ySS * -1;
        }
        matrix.get(matrixFB);
        GL33.glUniformMatrix4fv(uniform, false, matrixFB);
    }

    private static void loadImage() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            ByteBuffer img = STBImage.stbi_load("ss.png", w, h, comp, 3); // credit to someone from github
            if (img != null) {
                img.flip();

                GL33.glBindTexture(GL33.GL_TEXTURE_2D, textureId);
                GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGB, w.get(), h.get(), 0, GL33.GL_RGB, GL33.GL_UNSIGNED_BYTE, img);
                GL33.glGenerateMipmap(GL33.GL_TEXTURE_2D);

                STBImage.stbi_image_free(img);
            }
        }
    }

}
