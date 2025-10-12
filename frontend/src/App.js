import React, { useState, useEffect } from 'react';
import './App.css';
import HabitCalendar from './components/HabitCalendar';
import Login from './components/Login';
import { authService } from './services/authService';

const API_URL = 'http://localhost:8080/api/v1/habits';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(authService.isAuthenticated());
  const [currentUser, setCurrentUser] = useState(authService.getCurrentUser());
  const [habits, setHabits] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [editingHabit, setEditingHabit] = useState(null);
  const [selectedHabitForCalendar, setSelectedHabitForCalendar] = useState(null);
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    tags: '',
    star: false
  });

  // Handle login success
  const handleLoginSuccess = (userData) => {
    setIsAuthenticated(true);
    setCurrentUser(userData);
    setLoading(true);
    setError(null);
  };

  // Handle logout
  const handleLogout = () => {
    authService.logout();
    setIsAuthenticated(false);
    setCurrentUser(null);
    setHabits([]);
  };

  // Fetch habits from backend
  const fetchHabits = () => {
    if (!currentUser || !currentUser.userId) {
      setLoading(false);
      return;
    }

    fetch(`${API_URL}?userId=${currentUser.userId}`, {
      headers: {
        ...authService.getAuthHeader()
      }
    })
      .then(response => {
        if (response.status === 401 || response.status === 403) {
          // Token is invalid or expired
          authService.logout();
          setIsAuthenticated(false);
          setCurrentUser(null);
          throw new Error('Session expired. Please login again.');
        }
        if (!response.ok) {
          throw new Error('Failed to fetch habits');
        }
        return response.json();
      })
      .then(data => {
        setHabits(data);
        setLoading(false);
      })
      .catch(err => {
        setError(err.message);
        setLoading(false);
      });
  };

  useEffect(() => {
    if (isAuthenticated && currentUser) {
      fetchHabits();
    } else {
      setLoading(false);
    }
  }, [isAuthenticated, currentUser]);

  // Handle form input changes
  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  // Create new habit
  const handleCreate = (e) => {
    e.preventDefault();
    fetch(`${API_URL}?userId=${currentUser.userId}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...authService.getAuthHeader()
      },
      body: JSON.stringify(formData)
    })
      .then(response => {
        if (!response.ok) throw new Error('Failed to create habit');
        return response.json();
      })
      .then(() => {
        fetchHabits();
        setShowForm(false);
        setFormData({ title: '', description: '', tags: '', star: false });
      })
      .catch(err => alert('Error: ' + err.message));
  };

  // Update existing habit
  const handleUpdate = (e) => {
    e.preventDefault();
    fetch(`${API_URL}/${editingHabit.id}?userId=${currentUser.userId}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        ...authService.getAuthHeader()
      },
      body: JSON.stringify(formData)
    })
      .then(response => {
        if (!response.ok) throw new Error('Failed to update habit');
        fetchHabits();
        setEditingHabit(null);
        setFormData({ title: '', description: '', tags: '', star: false });
      })
      .catch(err => alert('Error: ' + err.message));
  };

  // Delete habit
  const handleDelete = (habitId) => {
    if (!window.confirm('Are you sure you want to delete this habit?')) return;

    fetch(`${API_URL}/${habitId}?userId=${currentUser.userId}`, {
      method: 'DELETE',
      headers: {
        ...authService.getAuthHeader()
      }
    })
      .then(response => {
        if (!response.ok) throw new Error('Failed to delete habit');
        fetchHabits();
      })
      .catch(err => alert('Error: ' + err.message));
  };

  // Start editing a habit
  const startEdit = (habit) => {
    setEditingHabit(habit);
    setFormData({
      title: habit.title,
      description: habit.description || '',
      tags: habit.tags || '',
      star: habit.star
    });
    setShowForm(true);
  };

  // Cancel form
  const cancelForm = () => {
    setShowForm(false);
    setEditingHabit(null);
    setFormData({ title: '', description: '', tags: '', star: false });
  };

  // Show login page if not authenticated
  if (!isAuthenticated) {
    return <Login onLoginSuccess={handleLoginSuccess} />;
  }

  if (loading) return <div className="App">Loading...</div>;
  if (error) return <div className="App">Error: {error}</div>;

  return (
    <div className="App">
      <header className="App-header">
        <h1>Habit Tracker</h1>
        <div className="user-info">
          <span>{currentUser.email}</span>
          <button className="btn btn-logout" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </header>

      <main className="container">
        <div className="header-actions">
          <h2>My Habits</h2>
          {!showForm && (
            <button
              className="btn btn-primary"
              onClick={() => setShowForm(true)}
            >
              + New Habit
            </button>
          )}
        </div>

        {showForm && (
          <div className="form-card">
            <h3>{editingHabit ? 'Edit Habit' : 'Create New Habit'}</h3>
            <form onSubmit={editingHabit ? handleUpdate : handleCreate}>
              <div className="form-group">
                <label>Title *</label>
                <input
                  type="text"
                  name="title"
                  value={formData.title}
                  onChange={handleInputChange}
                  required
                  maxLength={50}
                />
              </div>

              <div className="form-group">
                <label>Description</label>
                <textarea
                  name="description"
                  value={formData.description}
                  onChange={handleInputChange}
                  maxLength={1000}
                  rows={3}
                />
              </div>

              <div className="form-group">
                <label>Tags (comma-separated)</label>
                <input
                  type="text"
                  name="tags"
                  value={formData.tags}
                  onChange={handleInputChange}
                  placeholder="e.g. health,fitness,morning"
                />
              </div>

              <div className="form-group checkbox">
                <label>
                  <input
                    type="checkbox"
                    name="star"
                    checked={formData.star}
                    onChange={handleInputChange}
                  />
                  <span>⭐ Mark as favorite</span>
                </label>
              </div>

              <div className="form-actions">
                <button type="submit" className="btn btn-primary">
                  {editingHabit ? 'Update' : 'Create'}
                </button>
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={cancelForm}
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        )}

        {habits.length === 0 ? (
          <p className="empty-state">No habits found. Create your first habit!</p>
        ) : (
          <div className="habits-grid">
            {habits.map(habit => (
              <div key={habit.id} className="habit-card">
                <div className="habit-card-header">
                  <h3>
                    {habit.star && <span className="star">⭐</span>}
                    {habit.title}
                  </h3>
                  <div className="habit-actions">
                    <button
                      className="btn-icon"
                      onClick={() => startEdit(habit)}
                      title="Edit"
                    >
                      ✏️
                    </button>
                    <button
                      className="btn-icon"
                      onClick={() => handleDelete(habit.id)}
                      title="Delete"
                    >
                      🗑️
                    </button>
                  </div>
                </div>
                <p className="description">{habit.description}</p>
                <div className="tags">
                  {habit.tags && habit.tags.split(',').map((tag, idx) => (
                    <span key={idx} className="tag">{tag.trim()}</span>
                  ))}
                </div>
                <div className="status">
                  Status: <span className={`badge ${habit.status.toLowerCase()}`}>
                    {habit.status}
                  </span>
                </div>
                <button
                  className="btn btn-calendar"
                  onClick={() => setSelectedHabitForCalendar(habit)}
                >
                  📅 View Calendar
                </button>
              </div>
            ))}
          </div>
        )}
      </main>

      {selectedHabitForCalendar && (
        <HabitCalendar
          habit={selectedHabitForCalendar}
          userId={currentUser.userId}
          onClose={() => setSelectedHabitForCalendar(null)}
        />
      )}
    </div>
  );
}

export default App;
