package com.example.aima_id_app.ui.view

import android.graphics.Color
import java.util.Calendar
import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener
import com.example.aima_id_app.R
import com.example.aima_id_app.ui.viewmodel.CalendarViewModel
import com.example.aima_id_app.ui.viewmodel.SchedulingViewModel
import com.example.aima_id_app.ui.viewmodel.StatusViewModel
import com.example.aima_id_app.util.enums.PossibleScheduling
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate


class SchedulingFragment : Fragment() {

    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var schedulingViewModel: SchedulingViewModel
    private lateinit var statusViewModel: StatusViewModel
    private lateinit var nameServiceInput: Spinner
    private lateinit var cityInput: Spinner
    private lateinit var calendarView: CalendarView
    private lateinit var cityUnitInput: Spinner
    private lateinit var hourServiceInput: Spinner
    private lateinit var registerSchedulingButton: Button
    private var selectedDate: LocalDate? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View? {
        val view = inflater.inflate(R.layout.fragment_scheduling, container, false)

        // Inicializa os ViewModels
        calendarViewModel = ViewModelProvider(requireActivity())[CalendarViewModel::class.java]
        schedulingViewModel = ViewModelProvider(requireActivity())[SchedulingViewModel::class.java]
        statusViewModel = ViewModelProvider(requireActivity())[StatusViewModel::class.java]

        // Inicializa as views
        initializeViews(view)
        setupSpinners()
        setupCalendar()
        setupRegisterButton()

        /*val calendarView = view.findViewById<CalendarView>(R.id.calendarView)

// Definir a cor de fundo do dia atual
        calendarView.setTodayBackgroundColor(Color.parseColor("#FF4081"))

// Definir a cor do texto da data selecionada
        calendarView.setSelectedDateTextColor(Color.WHITE)

// Definir a cor do texto dos dias da semana
        calendarView.setWeekDayTextColor(Color.parseColor("#FF0000"))

// Definir a cor do texto das datas
        calendarView.setDateTextColor(Color.BLACK)

// Definir a cor do texto do cabeçalho (nome do mês e ano)
        calendarView.setHeaderTextColor(Color.parseColor("#0000FF"))

// Configurações adicionais (se necessário):
        calendarView.setSelectionColor(Color.parseColor("#00FF00")) // Cor da data selecionada
        calendarView.setFirstDayOfWeek(Calendar.MONDAY) // Definir segunda-feira como o primeiro dia da semana*/


        return view
    }

    private fun initializeViews(view: View) {
        nameServiceInput = view.findViewById(R.id.nameService_input)
        cityInput = view.findViewById(R.id.city_input)
        calendarView = view.findViewById(R.id.calendarView)
        cityUnitInput = view.findViewById(R.id.cityUnit_input)
        hourServiceInput = view.findViewById(R.id.hourService_input)
        registerSchedulingButton = view.findViewById(R.id.registerSchedulingButton)
    }

    private fun setupSpinners() {
        // Observa a lista de processos/serviços
        statusViewModel.processes.observe(viewLifecycleOwner) { processes ->
            // Converte o mapa de processos em uma lista de nomes ou títulos
            val services = processes.values.map { process -> process.serviceCode }

            if (services.isNotEmpty()) {
                val serviceAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    services
                ).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
                nameServiceInput.adapter = serviceAdapter
            }
        }


        // Observa a lista de cidades
        calendarViewModel.cities.observe(viewLifecycleOwner) { cities ->
            if (cities != null && cities.isNotEmpty()) {
                val cityAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    cities
                ).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
                cityInput.adapter = cityAdapter
            }
        }

        // Configura o listener do Spinner de cidades
        cityInput.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                parent?.getItemAtPosition(position)?.toString()?.let { selectedCity ->
                    Log.d("SchedulingFragment", "Cidade selecionada: $selectedCity")

                    selectedDate?.let { date ->
                        updateAvailableUnits(selectedCity, date)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("SchedulingFragment", "Nenhuma cidade selecionada")
            }
        }
    }

    private fun setupCalendar() {
        calendarView.setOnCalendarDayClickListener(object : OnCalendarDayClickListener {
            override fun onClick(calendarDay: CalendarDay) {
                val calendar = calendarDay.calendar
                selectedDate = LocalDate.of(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH)
                )

                Log.d("SchedulingFragment", "Selected date set to: $selectedDate")

                cityInput.selectedItem?.toString()?.let { selectedCity ->
                    updateAvailableUnits(selectedCity, selectedDate!!)
                }
            }
        })
    }

    private fun updateAvailableUnits(selectedCity: String, date: LocalDate) {
        schedulingViewModel.mapAvailableTimeByCityUnits(
            selectedCity,
            date
        ) { availableTimeByUnit ->
            Log.d("SchedulingFragment", "Available times received for city: $selectedCity and date: $date")

            val unitIds = availableTimeByUnit.keys.toList()
            if (unitIds.isNotEmpty()) {
                val unitAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    unitIds
                ).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
                cityUnitInput.adapter = unitAdapter

                setupUnitSpinner(availableTimeByUnit)
            }
        }
    }

    private fun setupUnitSpinner(availableTimeByUnit: MutableMap<String, MutableList<PossibleScheduling>>) {
        cityUnitInput.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                parent?.getItemAtPosition(position)?.toString()?.let { selectedUnitId ->
                    val availableTimes = availableTimeByUnit[selectedUnitId] ?: emptyList()

                    val hourAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        availableTimes
                    ).apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                    hourServiceInput.adapter = hourAdapter
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                hourServiceInput.adapter = null
            }
        }
    }

    private fun setupRegisterButton() {
        registerSchedulingButton.setOnClickListener {
            selectedDate?.let { date ->
                val selectedServiceName = nameServiceInput.selectedItem as String
                val selectedCity = cityInput.selectedItem as String
                val selectedUnitId = cityUnitInput.selectedItem as String
                val selectedHour = hourServiceInput.selectedItem as? PossibleScheduling
                    ?: run {
                        Snackbar.make(requireView(), "Selecione um horário válido.", Snackbar.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                schedulingViewModel.saveAppointment(
                    selectedServiceName,
                    selectedUnitId,
                    date,
                    selectedHour
                ) { success ->
                    if (success) {
                        Snackbar.make(requireView(), "Agendamento salvo com sucesso!", Snackbar.LENGTH_SHORT).show()

                    } else {
                        Snackbar.make(requireView(), "Erro ao salvar o agendamento.", Snackbar.LENGTH_SHORT).show()
                    }
                }
            } ?: Snackbar.make(requireView(), "Selecione uma data válida.", Snackbar.LENGTH_SHORT).show()

        }
    }
}




