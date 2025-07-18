// Since there are only two IDs, I decided to store the id and competition names here in convenience
// TODO: use to IDs from database, but not necessary rn
export const COMPETITIONS = [
  { id: 0, name: 'FLL Competition' },
  { id: 1, name: 'WRO Competition' }
]

// simplified team-competition array
export const TEAMS = [
  { id: 1, name: 'FLL Robot Game Team 1', competition: 1 },
  { id: 2, name: 'FLL Robot Game Team 2', competition: 1 },
  { id: 3, name: 'FLL Robot Game Team 3', competition: 1 },
  { id: 4, name: 'FLL Quarter Final Team 1', competition: 2 },
  { id: 5, name: 'FLL Quarter Final Team 2', competition: 2 },
  { id: 6, name: 'FLL Quarter Final Team 3', competition: 2 },
  { id: 7, name: 'FLL Test Round Team 1', competition: 3 },
  { id: 8, name: 'FLL Test Round Team 2', competition: 3 },
  { id: 9, name: 'FLL Test Round Team 3', competition: 3 },
  { id: 10, name: 'WRO Starter Team 1' },
  { id: 11, name: 'WRO Starter Team 2' },
  { id: 12, name: 'WRO Starter Team 3' },
  { id: 13, name: 'WRO RoboMission 2025 Team 1' },
  { id: 14, name: 'WRO RoboMission 2025 Team 2' },
  { id: 15, name: 'WRO RoboMission 2025 Team 3' }
]
