package com.example.fivepointlaplacian;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class FivePointLaplacian extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Parameters for the grid
        int p = 5;  // You can change p to 2, 3, or 4 for different resolutions
        int N = (int) Math.pow(2, p);
        double h = 1.0 / N;
        int gridSize = (N - 1) * (N - 1);

        double[][] A = new double[gridSize][gridSize];
        double[] b = new double[gridSize];
        double[] x = new double[N - 1];
        double[] y = new double[N - 1];

        // Generate grid points
        for (int i = 0; i < N - 1; i++) {
            x[i] = (i + 1) * h;
            y[i] = (i + 1) * h;
        }

        // Construct the matrix A and vector b using the 5-point Laplacian
        for (int i = 0; i < N - 1; i++) {
            for (int j = 0; j < N - 1; j++) {
                int k = i * (N - 1) + j;
                A[k][k] = -4; // Main diagonal

                if (i > 0) A[k][k - (N - 1)] = 1; // Upper diagonal
                if (i < N - 2) A[k][k + (N - 1)] = 1; // Lower diagonal
                if (j > 0) A[k][k - 1] = 1; // Left diagonal
                if (j < N - 2) A[k][k + 1] = 1; // Right diagonal

                // Construct the right-hand side b
                if (i == 0) b[k] -= 1; // Boundary condition: u(x, 0) = 1
            }
        }

        // Solve the linear system using Gaussian elimination
        double[] u = gaussianElimination(A, b);

        // Create a scatter plot using JavaFX to visualize the solution
        NumberAxis xAxis = new NumberAxis(0, 1, h);
        NumberAxis yAxis = new NumberAxis(0, 1, h);
        ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
        scatterChart.setTitle("Steady-State Temperature Distribution");

        // Add data to the scatter plot
        for (int i = 0; i < N - 1; i++) {
            for (int j = 0; j < N - 1; j++) {
                double xPos = x[j];
                double yPos = y[i];
                double temperature = u[i * (N - 1) + j];

                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.getData().add(new XYChart.Data<>(xPos, yPos, temperature));
                scatterChart.getData().add(series);
            }
        }

        // Create the scene and show the stage
        Pane root = new Pane();
        root.getChildren().add(scatterChart);
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Steady-State Temperature Distribution Visualization");
        primaryStage.show();
    }

    // Gaussian elimination method to solve Ax = b
    public static double[] gaussianElimination(double[][] A, double[] b) {
        int N = b.length;

        // Forward elimination
        for (int k = 0; k < N; k++) {
            // Find the pivot
            int max = k;
            for (int i = k + 1; i < N; i++) {
                if (Math.abs(A[i][k]) > Math.abs(A[max][k])) {
                    max = i;
                }
            }

            // Swap rows in A and b
            double[] tempRow = A[k];
            A[k] = A[max];
            A[max] = tempRow;
            double temp = b[k];
            b[k] = b[max];
            b[max] = temp;

            // Eliminate entries below the pivot
            for (int i = k + 1; i < N; i++) {
                double factor = A[i][k] / A[k][k];
                b[i] -= factor * b[k];
                for (int j = k; j < N; j++) {
                    A[i][j] -= factor * A[k][j];
                }
            }
        }

        // Back substitution
        double[] x = new double[N];
        for (int i = N - 1; i >= 0; i--) {
            double sum = 0;
            for (int j = i + 1; j < N; j++) {
                sum += A[i][j] * x[j];
            }
            x[i] = (b[i] - sum) / A[i][i];
        }
        return x;
    }
}
