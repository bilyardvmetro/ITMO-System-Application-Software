package com.weblab2.weblab2;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.weblab2.weblab2.Validator.*;

@WebServlet("/controller")
public class ControllerServlet extends HttpServlet {
    private final String ERROR_MESSAGE = "Sry. Smth went wrong \n Cause: %s";

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        var x = request.getParameter("x");
        var y = request.getParameter("y");
        var r = request.getParameter("r");

        if ((x != null && y != null && r != null)) {
            try {
                float parsedX = Float.parseFloat(x);
                float parsedY = Float.parseFloat(y);
                float parsedR = Float.parseFloat(r);

                System.out.println(parsedX + " " + parsedY + " " + parsedR);

                var xCheck = checkX(parsedX);
                var yCheck = checkY(parsedY);
                var rCheck = checkR(parsedR);

                System.out.println(xCheck + " " + yCheck + " " + rCheck);

                if (!(xCheck || yCheck || rCheck)){
                    System.out.println("goto calc");
                    request.getRequestDispatcher("/checkArea").forward(request, response);
                } else {
                    if (xCheck) {
                        request.setAttribute("error", String.format(ERROR_MESSAGE, "Unexpected X value"));
                        request.getRequestDispatcher("/error.jsp").forward(request, response);
                    }
                    if (yCheck) {
                        request.setAttribute("error", String.format(ERROR_MESSAGE, "Unexpected Y value"));
                        request.getRequestDispatcher("/error.jsp").forward(request, response);
                    }
                    if (rCheck) {
                        request.setAttribute("error", String.format(ERROR_MESSAGE, "Unexpected R value"));
                        request.getRequestDispatcher("/error.jsp").forward(request, response);
                    }
                }

            } catch (NumberFormatException e) {
                request.setAttribute("error", String.format(ERROR_MESSAGE, "Unexpected inputs"));
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            }

        } else {
            request.setAttribute("error", String.format(ERROR_MESSAGE, "Values are required"));
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
}
