import React, { useState, useEffect } from 'react';
import { habitCompletionService } from '../services/habitCompletionService';
import './HabitCalendar.css';

const HabitCalendar = ({ habit, userId, onClose }) => {
  const [currentMonth, setCurrentMonth] = useState(new Date());
  const [completions, setCompletions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedDate, setSelectedDate] = useState(null);
  const [noteText, setNoteText] = useState('');

  useEffect(() => {
    fetchCompletions();
  }, [currentMonth, habit.id]);

  const fetchCompletions = async () => {
    setLoading(true);
    try {
      const startOfMonth = new Date(currentMonth.getFullYear(), currentMonth.getMonth(), 1);
      const endOfMonth = new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1, 0);

      const startDate = startOfMonth.toISOString();
      const endDate = endOfMonth.toISOString();

      const data = await habitCompletionService.getCompletionsByDateRange(
        userId,
        habit.id,
        startDate,
        endDate
      );
      setCompletions(data);
    } catch (error) {
      console.error('Error fetching completions:', error);
    } finally {
      setLoading(false);
    }
  };

  const getDaysInMonth = () => {
    const year = currentMonth.getFullYear();
    const month = currentMonth.getMonth();
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const daysInMonth = lastDay.getDate();
    const startingDayOfWeek = firstDay.getDay();

    const days = [];

    // Add empty cells for days before the first day of the month
    for (let i = 0; i < startingDayOfWeek; i++) {
      days.push(null);
    }

    // Add all days in the month
    for (let day = 1; day <= daysInMonth; day++) {
      days.push(new Date(year, month, day));
    }

    return days;
  };

  const getCompletionForDate = (date) => {
    if (!date) return null;

    const dateStr = date.toISOString().split('T')[0];
    return completions.find(c => {
      const completionDate = new Date(c.completionDate).toISOString().split('T')[0];
      return completionDate === dateStr;
    });
  };

  const handleDateClick = async (date) => {
    if (!date) return;

    const completion = getCompletionForDate(date);

    if (completion) {
      // Toggle completion status
      try {
        await habitCompletionService.updateCompletion(
          userId,
          completion.id,
          habit.id,
          date.toISOString(),
          !completion.done,
          completion.note
        );
        await fetchCompletions();
      } catch (error) {
        alert('Error updating completion: ' + error.message);
      }
    } else {
      // Create new completion
      try {
        await habitCompletionService.createCompletion(
          userId,
          habit.id,
          date.toISOString(),
          true,
          ''
        );
        await fetchCompletions();
      } catch (error) {
        alert('Error creating completion: ' + error.message);
      }
    }
  };

  const handleDateRightClick = (e, date) => {
    e.preventDefault();
    if (!date) return;

    const completion = getCompletionForDate(date);
    setSelectedDate({ date, completion });
    setNoteText(completion?.note || '');
  };

  const handleSaveNote = async () => {
    if (!selectedDate) return;

    const { date, completion } = selectedDate;

    try {
      if (completion) {
        await habitCompletionService.updateCompletion(
          userId,
          completion.id,
          habit.id,
          date.toISOString(),
          completion.done,
          noteText
        );
      } else {
        await habitCompletionService.createCompletion(
          userId,
          habit.id,
          date.toISOString(),
          true,
          noteText
        );
      }
      await fetchCompletions();
      setSelectedDate(null);
      setNoteText('');
    } catch (error) {
      alert('Error saving note: ' + error.message);
    }
  };

  const previousMonth = () => {
    setCurrentMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() - 1));
  };

  const nextMonth = () => {
    setCurrentMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1));
  };

  const monthName = currentMonth.toLocaleString('default', { month: 'long', year: 'numeric' });
  const days = getDaysInMonth();
  const weekDays = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

  const completionCount = completions.filter(c => c.done).length;
  const completionRate = days.filter(d => d !== null).length > 0
    ? Math.round((completionCount / days.filter(d => d !== null).length) * 100)
    : 0;

  return (
    <div className="calendar-modal">
      <div className="calendar-modal-content">
        <div className="calendar-header">
          <div>
            <h2>{habit.title}</h2>
            <p className="calendar-stats">
              {completionCount} completions this month ({completionRate}%)
            </p>
          </div>
          <button className="close-btn" onClick={onClose}>×</button>
        </div>

        <div className="calendar-navigation">
          <button onClick={previousMonth}>‹</button>
          <h3>{monthName}</h3>
          <button onClick={nextMonth}>›</button>
        </div>

        {loading ? (
          <div className="calendar-loading">Loading...</div>
        ) : (
          <div className="calendar-grid">
            {weekDays.map(day => (
              <div key={day} className="calendar-weekday">{day}</div>
            ))}
            {days.map((date, index) => {
              const completion = getCompletionForDate(date);
              const isToday = date && date.toDateString() === new Date().toDateString();

              return (
                <div
                  key={index}
                  className={`calendar-day ${!date ? 'empty' : ''} ${
                    completion?.done ? 'completed' : ''
                  } ${isToday ? 'today' : ''}`}
                  onClick={() => handleDateClick(date)}
                  onContextMenu={(e) => handleDateRightClick(e, date)}
                  title={date ? `Click to toggle, right-click to add note` : ''}
                >
                  {date && (
                    <>
                      <span className="day-number">{date.getDate()}</span>
                      {completion?.note && <span className="has-note">📝</span>}
                    </>
                  )}
                </div>
              );
            })}
          </div>
        )}

        <div className="calendar-legend">
          <div className="legend-item">
            <div className="legend-box completed"></div>
            <span>Completed</span>
          </div>
          <div className="legend-item">
            <div className="legend-box today"></div>
            <span>Today</span>
          </div>
          <div className="legend-item">
            <span>Click to toggle • Right-click to add note</span>
          </div>
        </div>
      </div>

      {selectedDate && (
        <div className="note-modal" onClick={() => setSelectedDate(null)}>
          <div className="note-modal-content" onClick={(e) => e.stopPropagation()}>
            <h3>Add Note for {selectedDate.date.toLocaleDateString()}</h3>
            <textarea
              value={noteText}
              onChange={(e) => setNoteText(e.target.value)}
              placeholder="Enter your note here..."
              rows={4}
            />
            <div className="note-modal-actions">
              <button className="btn btn-primary" onClick={handleSaveNote}>
                Save
              </button>
              <button
                className="btn btn-secondary"
                onClick={() => setSelectedDate(null)}
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default HabitCalendar;