package com.example.aima_id_app.ui.view

import java.util.Calendar
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.Spinner

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.aima_id_app.R
import com.example.aima_id_app.data.model.db_model.AimaUnit
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


    /**
     * Initializes the SchedulingFragment view.
     *
     * Inflates the layout, initializes ViewModels, sets up UI components,
     * and disables weekends in the calendar.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_scheduling, container, false)

        calendarViewModel = ViewModelProvider(requireActivity())[CalendarViewModel::class.java]
        schedulingViewModel = ViewModelProvider(requireActivity())[SchedulingViewModel::class.java]
        statusViewModel = ViewModelProvider(requireActivity())[StatusViewModel::class.java]

        initializeViews(view)
        setupSpinners()
        setupCalendar()
        setupRegisterButton()

        calendarView = view.findViewById(R.id.calendarView)

        val disabledDays = mutableListOf<Calendar>()
        val calendar = Calendar.getInstance()
        calendar.set(2024, Calendar.JANUARY, 1)

        val today = Calendar.getInstance().timeInMillis
        calendarView.minDate = today

        while (calendar.get(Calendar.YEAR) == 2024) {
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                disabledDays.add(calendar.clone() as Calendar)
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return view
    }

    /**
     * Initializes UI views within the SchedulingFragment.
     *
     * Finds and assigns references to UI elements using their resource IDs.
     *
     * @param view The root view of the fragment.
     */
    private fun initializeViews(view: View) {
        nameServiceInput = view.findViewById(R.id.nameService_input)
        cityInput = view.findViewById(R.id.city_input)
        calendarView = view.findViewById(R.id.calendarView)
        cityUnitInput = view.findViewById(R.id.cityUnit_input)
        hourServiceInput = view.findViewById(R.id.hourService_input)
        registerSchedulingButton = view.findViewById(R.id.registerSchedulingButton)
    }

    /**
     * Configures and populates the spinners in the SchedulingFragment.
     *
     * Sets up the nameServiceInput and cityInput spinners with data
     * and listeners for item selection.
     */
    private fun setupSpinners() {

        statusViewModel.getProcessesByUserId() { listaDePares ->

            val valores = listaDePares.map { it.second }

            val adapter = ArrayAdapter(
                this.requireContext(),
                android.R.layout.simple_spinner_item,
                valores
            )

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            nameServiceInput.adapter = adapter

            nameServiceInput.onItemSelectedListener  =   object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val (key, value) = listaDePares[position]
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
        }


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

        cityInput.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                parent?.getItemAtPosition(position)?.toString()?.let { selectedCity ->

                    selectedDate?.let { date ->
                        updateAvailableUnits(selectedCity, date)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    /**
     * Configures the calendar view and handles date selection.
     *
     * Sets up a date change listener to detect when a date is selected
     * and performs actions based on the selected date.
     */
    private fun setupCalendar() {
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            val dayOfWeek = selectedDate.dayOfWeek.value

            if (dayOfWeek == 6 || dayOfWeek == 7) {
                Snackbar.make(requireView(), "Sábado ou domingo não são permitidos.", Snackbar.LENGTH_SHORT).show()

            } else {

                this.selectedDate = selectedDate

                cityInput.selectedItem?.toString()?.let { selectedCity ->
                    updateAvailableUnits(selectedCity, selectedDate!!)
                }
            }
        }
    }


    /**
     * Updates the available units based on the selected city and date.
     *
     * Retrieves available units from the schedulingViewModel and updates
     * the cityUnitInput spinner accordingly.
     *
     * @param selectedCity The selected city.
     * @param date The selected date.
     */
    private fun updateAvailableUnits(selectedCity: String, date: LocalDate) {
        schedulingViewModel.mapAvailableTimeByCityUnits(
            selectedCity,
            date
        ) { availableTimeByUnit ->

            val unit = availableTimeByUnit.keys.toList()

            if (unit.isNotEmpty()) {
                val unitAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    unit
                ).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
                cityUnitInput.adapter = unitAdapter

                setupUnitSpinner(availableTimeByUnit)
            }
        }
    }

    /**
     * Configures and populates the unit spinner with available times.
     *
     * Sets up the cityUnitInput spinner with unit names and an item selection
     * listener to update the hourServiceInput spinner with available times
     * for the selected unit.
     *
     * @param availableTimeByUnit A map containing available times for each unit.
     */
    private fun setupUnitSpinner(availableTimeByUnit: MutableMap<AimaUnit, MutableList<PossibleScheduling>>) {
        val units = availableTimeByUnit.keys.toList()

        val unitNames = units.map { it.name }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            unitNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cityUnitInput.adapter = adapter

        cityUnitInput.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedUnit = units[position]

                val availableTimes = availableTimeByUnit[selectedUnit] ?: emptyList()
                val mapTimes = availableTimes.map { it.time }

                val hourAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    mapTimes
                ).apply {
                    setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                }
                hourServiceInput.adapter = hourAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                hourServiceInput.adapter = null
            }
        }
    }


    /**
     * Configures the register button and handles appointment saving.
     *
     * Sets up an onClickListener for the registerSchedulingButton to save
     * the appointment details when clicked.
     */
    private fun setupRegisterButton() {

        registerSchedulingButton.setOnClickListener {
            selectedDate?.let { date ->

                val selectedServiceName = nameServiceInput.selectedItem as String
                val selectedCity = cityInput.selectedItem as String
                val selectedUnitId = cityUnitInput.selectedItem as String
                val selectedHour = PossibleScheduling.fromTime(hourServiceInput.selectedItem.toString()) as? PossibleScheduling
                    ?: run {
                        Snackbar.make(requireView(), "Selecione um horário válido.", Snackbar.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                schedulingViewModel.saveAppointment(
                    selectedServiceName,
                    selectedUnitId,
                    date,
                    selectedHour.time
                ) { success ->

                    if (success) {
                        Snackbar.make(requireView(), "Agendamento salvo com sucesso!", Snackbar.LENGTH_SHORT).show()
                        Handler(Looper.getMainLooper()).postDelayed({
                            requireActivity().supportFragmentManager.popBackStack()
                        }, 1000)

                    } else {
                        Snackbar.make(requireView(), "Erro ao salvar o agendamento.", Snackbar.LENGTH_SHORT).show()
                    }
                }
            } ?: Snackbar.make(requireView(), "Selecione uma data válida.", Snackbar.LENGTH_SHORT).show()

        }
    }
}




