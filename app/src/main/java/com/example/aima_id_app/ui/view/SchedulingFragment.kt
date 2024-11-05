package com.example.aima_id_app.ui.view

import android.app.DatePickerDialog
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.aima_id_app.R
import com.example.aima_id_app.ui.viewmodel.CalendarViewModel
import com.example.aima_id_app.ui.viewmodel.SchedulingViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.util.Calendar

class SchedulingFragment : Fragment() {

    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var schedulingViewModel: SchedulingViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var nameServiceInput: Spinner
    private lateinit var cityInput: Spinner
    private lateinit var freeDateInput: EditText
    private lateinit var cityUnitInput: Spinner
    private lateinit var hourServiceInput: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_scheduling, container, false)

        // Inicializa os ViewModels corretamente
        calendarViewModel = ViewModelProvider(requireActivity()).get(CalendarViewModel::class.java)
        schedulingViewModel = ViewModelProvider(requireActivity()).get(SchedulingViewModel::class.java)
        userViewModel = ViewModelProvider(requireActivity()).get(SchedulingViewModel::class.java)

        nameServiceInput = view.findViewById(R.id.nameService_input)
        cityInput = view.findViewById(R.id.city_input)
        freeDateInput = view.findViewById(R.id.freeDate_input)
        cityUnitInput = view.findViewById(R.id.cityUnit_input)
        hourServiceInput = view.findViewById(R.id.hourService_input)

        freeDateInput.setOnClickListener {
            showDatePickerDialog()
        }

        // Observa a lista de cidades do CalendarViewModel
        calendarViewModel.cities.observe(viewLifecycleOwner) { cities ->
            val cityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, cities)
            cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            cityInput.adapter = cityAdapter
        }

        // Listener para o Spinner de cidades
        cityInput.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCity = parent?.getItemAtPosition(position) as String
                // Aqui você pode carregar as unidades da cidade selecionada
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // ...
            }
        }

        return view
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Certifique-se de que o calendário e a cidade estão configurados
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, dayOfMonth ->
                val selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, dayOfMonth)
                val selectedCity = cityInput.selectedItem as String // Obtém a cidade selecionada

                // Obter datas indisponíveis
                calendarViewModel.getUnavailableDays(selectedCity, YearMonth.of(selectedYear, selectedMonth + 1)) { unavailableDays ->
                    // Verifique se a data selecionada está indisponível
                    if (unavailableDays.contains(dayOfMonth)) {
                        // Mostre uma mensagem de erro ou avise o usuário
                        Toast.makeText(requireContext(), "Data indisponível, escolha outra data.", Toast.LENGTH_SHORT).show()
                    } else {
                        // Defina a data no EditText
                        freeDateInput.setText(selectedDate.toString())

                        // Carregar horários disponíveis
                        schedulingViewModel.mapAvailableTimeByCityUnits(selectedCity, selectedDate) { availableTimeByUnit ->
                            val unitId = "" // Obter o ID da unidade selecionada
                            val availableTimes = availableTimeByUnit[unitId] ?: emptyList()

                            // Atualizar o Spinner de horários
                            val hourAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, availableTimes)
                            hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            hourServiceInput.adapter = hourAdapter
                        }
                    }
                }
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

}
