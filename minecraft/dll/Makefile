# Makefile for Unknown.dll

# Компилятор и флаги
CXX = g++
CXXFLAGS = -shared -O2 -Wall -std=c++11
LDFLAGS = -lgdi32 -luser32 -lkernel32 -lwinmm

# Имя выходной DLL
TARGET = Unknown.dll

# Исходные файлы
SOURCES = main.cpp

# Объектные файлы
OBJECTS = $(SOURCES:.cpp=.o)

# Правило по умолчанию
all: $(TARGET)

# Компиляция DLL
$(TARGET): $(OBJECTS)
	$(CXX) $(CXXFLAGS) -o $(TARGET) $(OBJECTS) $(LDFLAGS)
	@echo "DLL compiled successfully: $(TARGET)"

# Компиляция объектных файлов
%.o: %.cpp
	$(CXX) $(CXXFLAGS) -c $< -o $@

# Очистка
clean:
	rm *.o $(TARGET)

# Установка (копирование в нужную директорию)
install: $(TARGET)
	rm -f ../../libraries/natives/Unknown.dll
	mkdir -p ../../libraries/natives
	cp -f Unknown.dll ../../libraries/natives/Unknown.dll
	@echo "DLL installed to libraries/natives/"

.PHONY: all clean install
