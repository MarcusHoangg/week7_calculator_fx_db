import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CalculatorController {

    @FXML private TextField number1Field;
    @FXML private TextField number2Field;
    @FXML private Label resultLabel;

    @FXML
    private void onCalculateClick() {
        try {
            double num1 = Double.parseDouble(number1Field.getText().trim());
            double num2 = Double.parseDouble(number2Field.getText().trim());

            double sum = num1 + num2;
            double product = num1 * num2;
            double subtract = num1 - num2;

            // Division: handle divide-by-zero
            Double division = null;
            if (num2 != 0) {
                division = num1 / num2;
            }

            // Show results
            String divisionText = (division == null) ? "undefined (divide by 0)" : String.valueOf(division);
            resultLabel.setText(
                    "Sum: " + sum +
                            ", Product: " + product +
                            ", Subtract: " + subtract +
                            ", Division: " + divisionText
            );

            // Save to DB (store null if division undefined)
            ResultService.saveResult(num1, num2, sum, product, subtract, division);

        } catch (NumberFormatException e) {
            resultLabel.setText("Please enter valid numbers!");
        }
    }
}