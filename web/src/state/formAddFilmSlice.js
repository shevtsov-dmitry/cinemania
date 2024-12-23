const formAddFilmSlice = (set) => ({
  isFormAddFilmVisible: false,

  showFormAddFilm: () =>
    set(() => ({
      isFormAddFilmVisible: true,
    })),
  hideFormAddFilm: () =>
    set(() => ({
      isFormAddFilmVisible: false,
    })),
  toggleFormAddFilm: () =>
    set((state) => ({
      isFormAddFilmVisible: !state.isFormAddFilmVisible,
    })),
});

export default formAddFilmSlice;
