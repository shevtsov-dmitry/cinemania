import { create } from "zustand";

interface FormAddFilmState {
  isFormAddFilmVisible: boolean;
  showFormAddFilm: () => void;
  hideFormAddFilm: () => void;
  toggleFormAddFilm: () => void;
}

const useFormAddFilmStore = create<FormAddFilmState>((set) => ({
  isFormAddFilmVisible: false,
  showFormAddFilm: () => set(() => ({ isFormAddFilmVisible: true })),
  hideFormAddFilm: () => set(() => ({ isFormAddFilmVisible: false })),
  toggleFormAddFilm: () =>
    set((state) => ({
      isFormAddFilmVisible: !state.isFormAddFilmVisible,
    })),
}));

export default useFormAddFilmStore;