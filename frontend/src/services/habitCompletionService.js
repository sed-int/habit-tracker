const API_BASE_URL = 'http://localhost:8080/api/v1';

export const habitCompletionService = {
  // Create a new completion
  createCompletion: async (userId, habitId, completionDate, done, note) => {
    const response = await fetch(`${API_BASE_URL}/habit-completions?userId=${userId}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        habitId,
        completionDate,
        done,
        note: note || ''
      })
    });

    if (!response.ok) {
      throw new Error('Failed to create completion');
    }
    return response.json();
  },

  // Get all completions for a habit
  getCompletionsByHabit: async (userId, habitId) => {
    const response = await fetch(
      `${API_BASE_URL}/habit-completions/habit/${habitId}?userId=${userId}`
    );

    if (!response.ok) {
      throw new Error('Failed to fetch completions');
    }
    return response.json();
  },

  // Get completions for a habit within a date range
  getCompletionsByDateRange: async (userId, habitId, startDate, endDate) => {
    const response = await fetch(
      `${API_BASE_URL}/habit-completions/habit/${habitId}/range?userId=${userId}&startDate=${startDate}&endDate=${endDate}`
    );

    if (!response.ok) {
      throw new Error('Failed to fetch completions by date range');
    }
    return response.json();
  },

  // Update a completion
  updateCompletion: async (userId, completionId, habitId, completionDate, done, note) => {
    const response = await fetch(
      `${API_BASE_URL}/habit-completions/${completionId}?userId=${userId}`,
      {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          habitId,
          completionDate,
          done,
          note: note || ''
        })
      }
    );

    if (!response.ok) {
      throw new Error('Failed to update completion');
    }
  },

  // Delete a completion
  deleteCompletion: async (userId, completionId) => {
    const response = await fetch(
      `${API_BASE_URL}/habit-completions/${completionId}?userId=${userId}`,
      { method: 'DELETE' }
    );

    if (!response.ok) {
      throw new Error('Failed to delete completion');
    }
  },

  // Get completion count for a habit
  getCompletionCount: async (userId, habitId) => {
    const response = await fetch(
      `${API_BASE_URL}/habit-completions/habit/${habitId}/count?userId=${userId}`
    );

    if (!response.ok) {
      throw new Error('Failed to get completion count');
    }
    return response.json();
  }
};