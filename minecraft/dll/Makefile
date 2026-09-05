# Makefile for Unknown.dll

# Компилятор и флаги
CXX = g++
CXXFLAGS = -shared -O2 -Wall -std=c++11
LDFLAGS = -lgdi32 -luser32 -lkernel32

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
	rm -f *.o $(TARGET)

# Установка (копирование в нужную директорию)
install: $(TARGET)
	mkdir -p ../../libraries/natives
	cp Unknown.dll ../../libraries/natives/Unknown.dll
	@echo "DLL installed to libraries/natives/"

.PHONY: all clean install
