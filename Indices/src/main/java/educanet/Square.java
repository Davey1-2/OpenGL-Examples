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

    private static final int[] indices = {
            0, 1, 2,
            1, 2, 3,
    };


    private final int squareVaoId;
    public float[] imagePoints;
    public float cyborgPart = 0;
    public FloatBuffer bufferImg = BufferUtils.createFloatBuffer(8);


    public static int uniform;
    public Matrix4f matrix;
    public FloatBuffer matrixFB;


    private static int textureId;


    public Square(float x, float y, float width) {
        float[] vertices;
        squareVaoId = GL33.glGenVertexArrays();
        int squareVboId = GL33.glGenBuffers();
        int squareEboId = GL33.glGenBuffers();
        int colorsId = GL33.glGenBuffers();


        matrix = new Matrix4f().identity();
        matrixFB = BufferUtils.createFloatBuffer(16);
        textureId = GL33.glGenTextures();


        vertices = new float[]{
                0.5f, 0.5f, 0.0f, // 0 -> Top right
                0.5f, -0.5f, 0.0f, // 1 -> Bottom right
                -0.5f, -0.5f, 0.0f, // 2 -> Bottom left
                -0.5f, 0.5f, 0.0f, // 3 -> Top left
        };


        float[] colors = {
                1f, 1f, 1f, 1f,
                1f, 1f, 1f, 1f,
                1f, 1f, 1f, 1f,
                1f, 1f, 1f, 1f,
        };

        imagePoints = new float[]{
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f,
                0.0f, 0.0f
        };
        loadImage();
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

        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, fb, GL33.GL_STATIC_DRAW);
        GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(0);
        GL33.glUseProgram(Shaders.shaderProgramId);
        matrix.get(matrixFB);
        GL33.glUniformMatrix4fv(uniform, false, matrixFB);

        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, textureId);
        bufferImg.put(imagePoints).flip();
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, bufferImg, GL33.GL_STATIC_DRAW);
        GL33.glVertexAttribPointer(2, 2, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(2);
    }

    public void render() {
        matrix.get(matrixFB);
        GL33.glUniformMatrix4fv(uniform, false, matrixFB);
        GL33.glUseProgram(Shaders.shaderProgramId);
        GL33.glBindVertexArray(squareVaoId);
        GL33.glDrawElements(GL33.GL_TRIANGLES, indices.length, GL33.GL_UNSIGNED_INT, 0);
    }


    public void update(long window) {

        int imageCurr = (int) cyborgPart % 6;
        if (imageCurr == 0) {
            imagePoints = new float[]{
                    0.16f, 0.0f,
                    0.16f, 1f,
                    0.0f, 1f,
                    0.0f, 0.0f,
            };
        }
        if (imageCurr == 1) {
            imagePoints = new float[]{
                    0.32f, 0.0f,
                    0.32f, 1f,
                    0.16f, 1f,
                    0.16f, 0.0f,
            };
        }
        if (imageCurr == 2) {
            imagePoints = new float[]{
                    0.48f, 0.0f,
                    0.48f, 1f,
                    0.32f, 1f,
                    0.32f, 0.0f,
            };
        }
        if (imageCurr == 3) {
            imagePoints = new float[]{
                    0.64f, 0.0f,
                    0.64f, 1f,
                    0.48f, 1f,
                    0.48f, 0.0f,
            };
        }
        if (imageCurr == 4) {
            imagePoints = new float[]{
                    0.80f, 0.0f,
                    0.80f, 1f,
                    0.64f, 1f,
                    0.64f, 0.0f,
            };
        }
        if (imageCurr == 5) {
            imagePoints = new float[]{
                    0.96f, 0.0f,
                    0.96f, 1f,
                    0.8f, 1f,
                    0.8f, 0f,
            };
        }
        cyborgPart = cyborgPart + 0.05f;

        bufferImg.clear().put(imagePoints).flip();
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, textureId);
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, bufferImg, GL33.GL_STATIC_DRAW);
        GL33.glVertexAttribPointer(2, 2, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(2);

    }

    private static void loadImage() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            ByteBuffer img = STBImage.stbi_load("Cyborg_run.png", w, h, comp, 3); // credit to someone from github
            if (img != null) {
                img.flip();

                GL33.glBindTexture(GL33.GL_TEXTURE_2D, textureId);
                GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGB, w.get(), h.get(), 0, GL33.GL_RGB, GL33.GL_UNSIGNED_BYTE, img);
                GL33.glGenerateMipmap(GL33.GL_TEXTURE_2D);

            }
        }
    }

}
